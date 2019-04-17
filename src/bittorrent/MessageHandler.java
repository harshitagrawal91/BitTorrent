/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.ActualMessage;
import java.util.concurrent.ConcurrentLinkedQueue;
import bittorrent.beans.GlobalConstants;
import bittorrent.beans.PeerInfoConfigObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 */
public class MessageHandler extends Thread {

    private int peerId;
    Logger log;

    MessageHandler(int peerId) {
        this.log = GlobalConstants.log;
        this.peerId = peerId;
    }

    public ConcurrentLinkedQueue<ActualMessage> messageQueue = new ConcurrentLinkedQueue<ActualMessage>();

    public void run() {
        while (true) {
            if (!messageQueue.isEmpty()) {
                ActualMessage message = messageQueue.poll();
                if (message.getMessageType() == GlobalConstants.messageType.BITFIELD.getValue()) {
                    handleBitfieldMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.CHOKE.getValue()) {
                    handleChokeMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.UNCHOKE.getValue()) {
                    handleUnchokeMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.INTERESTED.getValue()) {
                    handleInterestedMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.NOT_INTERESTED.getValue()) {
                    handleNotInterestedMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.REQUEST.getValue()) {
                    handleRequestMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.PIECE.getValue()) {
                    handlePiecetMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.HAVE.getValue()) {
                    handleHaveMessage(message);
                }

            } else {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }
    }

    private void handleChokeMessage(ActualMessage message) {
        log.info("Peer " +Peer.currentPeerID+" is choked by " + Integer.toString(peerId));
        GlobalConstants.PEERLIST.get(peerId).setUnchockedForCurrentPeers(false);
    }

    private void handleBitfieldMessage(ActualMessage message) {
        GlobalConstants.PEERLIST.get(peerId).setChunks(BitSet.valueOf(message.getMessage()));

        // send currentPeer's bitfield to peer if it's not empty
        if (!Peer.currentPeer.getChunks().isEmpty()) {
            ActualMessage responseBitfieldMessage = new ActualMessage();
            responseBitfieldMessage.setMessageType(GlobalConstants.messageType.BITFIELD.getValue());
            responseBitfieldMessage.setMessage(Peer.currentPeer.getChunks().toByteArray());
            responseBitfieldMessage.setLength(responseBitfieldMessage.getMessage().length + 1);
            GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(responseBitfieldMessage);
        }

        // a:  0011
        // b:  1011
        // not a and b: 1100 and 1011 => 1000 => a is interested in b (in the first chunk)
        BitSet currentChunks = (BitSet) Peer.currentPeer.getChunks().clone();
        currentChunks.flip(0, (int) GlobalConstants.chunkCount);
        BitSet remoteChunks = (BitSet) GlobalConstants.PEERLIST.get(peerId).getChunks().clone();
        currentChunks.and(remoteChunks);

        Boolean interested = !currentChunks.isEmpty();

        ActualMessage interestedOrNotMessage = new ActualMessage();
        interestedOrNotMessage.setLength(1);

        if (interested) {
            interestedOrNotMessage.setMessageType(GlobalConstants.messageType.INTERESTED.getValue());
        } else {
            interestedOrNotMessage.setMessageType(GlobalConstants.messageType.NOT_INTERESTED.getValue());
        }

        GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(interestedOrNotMessage);

    }

    private void handleInterestedMessage(ActualMessage message) {

        GlobalConstants.interestedPeers.put(peerId, GlobalConstants.PEERLIST.get(peerId));
        log.info("Peer "+ Peer.currentPeerID +" received the 'interested' message from " + Integer.toString(peerId));
    }

    private void handleNotInterestedMessage(ActualMessage message) {
        log.info("Peer "+ Peer.currentPeerID +" received the 'not interested' message from " + Integer.toString(peerId));
    }

    private synchronized void sendRequestMessage() {
//        System.out.println(GlobalConstants.PEERLIST.get(peerId).getState());
        if (GlobalConstants.PEERLIST.get(peerId).isUnchockedForCurrentPeers()) {
            GlobalConstants.PEERLIST.get(peerId).startTime = System.nanoTime();
            BitSet currentPeerChunk = (BitSet) Peer.currentPeer.getChunks().clone();
            currentPeerChunk.flip(0, (int) GlobalConstants.chunkCount);
            BitSet remotePeerChunk = (BitSet) GlobalConstants.PEERLIST.get(peerId).getChunks().clone();
            remotePeerChunk.and(currentPeerChunk);
            for (int i = remotePeerChunk.nextSetBit(0); i >= 0; i = remotePeerChunk.nextSetBit(i + 1)) {
                if (!GlobalConstants.requestedChunks.contains(i)) {
                    GlobalConstants.requestedChunks.put(i, "");
                    ActualMessage request = new ActualMessage();
                    request.setLength(5);
                    request.setMessageType(GlobalConstants.messageType.REQUEST.getValue());
                    request.setMessage(ByteBuffer.allocate(4).putInt(i).array());
                    GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(request);
                    break;
                }
                if (i == Integer.MAX_VALUE) {
                    break;
                }
            }
        }
    }

    private void sendHaveMessage(byte[] chunkIdArr) {
        for (int pid : GlobalConstants.PEERLIST.keySet()) {
            PeerInfoConfigObject peer = GlobalConstants.PEERLIST.get(pid);
            if (peer != null) {
                ActualMessage haveMsg = new ActualMessage();
                haveMsg.setLength(5);
                haveMsg.setMessageType(GlobalConstants.messageType.HAVE.getValue());
                haveMsg.setMessage(chunkIdArr.clone());
                if (peer.getPeerHandler() != null) {
                    peer.getPeerHandler().sendMessage(haveMsg);
                } else {
                }

            } else {
            }
        }
    }

    private void handleHaveMessage(ActualMessage message) {
        int chunkId = ByteBuffer.wrap(message.getMessage()).getInt();
        log.info("Peer " + Peer.currentPeerID+ " received the 'have' message from peer " + Integer.toString(peerId) + " for the piece " + chunkId);
        GlobalConstants.PEERLIST.get(peerId).getChunks().set(chunkId);
        if (!Peer.currentPeer.getChunks().get(chunkId)) {
            ActualMessage interestedMsg = new ActualMessage();
            interestedMsg.setLength(1);
            interestedMsg.setMessageType(GlobalConstants.messageType.INTERESTED.getValue());
            GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(interestedMsg);
        }
    }

    private void handleUnchokeMessage(ActualMessage message) {
        log.info("Peer "+Peer.currentPeerID + " is unchoked by " + Integer.toString(peerId));
        GlobalConstants.PEERLIST.get(peerId).setUnchockedForCurrentPeers(true);
        sendRequestMessage();

    }

    private void handleRequestMessage(ActualMessage message) {
        int chunkid = ByteBuffer.wrap(message.getMessage()).getInt();
        try {
            byte[] fileChunk = Files.readAllBytes(new File(GlobalConstants.chunkDirectory + File.separator + chunkid + ".splitPart").toPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(message.getMessage());
            outputStream.write(fileChunk);
            ActualMessage pieceMessage = new ActualMessage();
            pieceMessage.setMessage(outputStream.toByteArray());
            pieceMessage.setLength(pieceMessage.getMessage().length + 1);
            pieceMessage.setMessageType(GlobalConstants.messageType.PIECE.getValue());
            GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(pieceMessage);

        } catch (IOException ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void handlePiecetMessage(ActualMessage message) {
        
        byte[] chunk = message.getMessage();
        byte[] chunkIdArr = new byte[4];
        byte[] fileChunk = new byte[chunk.length - 4];
        GlobalConstants.PEERLIST.get(peerId).finishTime = System.nanoTime();
        
        long diff = GlobalConstants.PEERLIST.get(peerId).finishTime - GlobalConstants.PEERLIST.get(peerId).startTime;
        double elapsed = TimeUnit.MILLISECONDS.convert(diff,TimeUnit.NANOSECONDS) / 1000.0;
        double downloadRate = 0;
        if(elapsed != 0)
        {
            downloadRate = message.getLength() / elapsed;
        }
        GlobalConstants.PEERLIST.get(peerId).downloadSpeed = downloadRate;
        for (int i = 0; i < chunk.length; i++) {
            if (i < 4) {
                chunkIdArr[i] = chunk[i];
            } else {
                fileChunk[i - 4] = chunk[i];
            }
        }
        int chunkId = ByteBuffer.wrap(chunkIdArr).getInt();
        log.info("Peer " + Peer.currentPeerID + " has downloaded the piece " + Integer.toString(chunkId) + " from " + Integer.toString(peerId)
                + ". Now the number of pieces it has is " + (GlobalConstants.chunkCount - Peer.currentPeer.getChunks().cardinality()));
        OutputStream os;
        try {
            os = new FileOutputStream(new File(GlobalConstants.chunkDirectory + File.separator + chunkId + ".splitPart"));
            os.write(fileChunk);
            os.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        Peer.currentPeer.getChunks().set(chunkId);
        if (Peer.currentPeer.getChunks().cardinality() == GlobalConstants.chunkCount) {
            log.info("Peer " + Peer.currentPeerID + " has downloaded the complete file");
        }

        sendHaveMessage(chunkIdArr);

        BitSet currentPeerChunk = (BitSet) Peer.currentPeer.getChunks().clone();
        currentPeerChunk.flip(0, (int) GlobalConstants.chunkCount);
        if (currentPeerChunk.isEmpty()) {
            File[] chunksFiles = new File[(int) GlobalConstants.chunkCount];
            List<byte[]> bytesList = new ArrayList<byte[]>();
            for (int i = 0; i < GlobalConstants.chunkCount; i++) {
                chunksFiles[i] = new File(GlobalConstants.chunkDirectory + File.separator + i + ".splitPart");
                try {
                    bytesList.add(Files.readAllBytes(chunksFiles[i].toPath()));
                } catch (IOException ex) {
                    Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            FileUtility.mergeFilesByByte(bytesList);
        }
        sendRequestMessage();
    }
}
