/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.ActualMessage;
import java.util.concurrent.ConcurrentLinkedQueue;
import bittorrent.beans.GlobalConstants;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsh
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
        GlobalConstants.PEERLIST.get(peerId).setUnchockedForCurrentPeers(false);
    }

    private void handleBitfieldMessage(ActualMessage message) {
        System.out.print("bitfield message received" + message.getLength());
        // TODO why do we have to set the peer's bitfield again, even when it was was set during handshake?
        GlobalConstants.PEERLIST.get(peerId).setChunks(BitSet.valueOf(message.getMessage()));

        log.info("received bitfield message from peer" + peerId + "--" + GlobalConstants.PEERLIST.get(peerId).getChunks());

        // send currentPeer's bitfield to peer if it's not empty
        if (!Peer.currentPeer.getChunks().isEmpty()) {
            ActualMessage responseBitfieldMessage = new ActualMessage();
            responseBitfieldMessage.setMessageType(GlobalConstants.messageType.BITFIELD.getValue());
            responseBitfieldMessage.setMessage(Peer.currentPeer.getChunks().toByteArray());
            responseBitfieldMessage.setLength(responseBitfieldMessage.getMessage().length + 1);
            GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(responseBitfieldMessage);
        }

        // TODO: find out if you don't have a chunk which your peer has
        // THIS IS A BUG, change
        // a:  0011
        // b:  1011
        // a & b == a means a is interested in b (provided a is not equal to b)
        BitSet a = Peer.currentPeer.getChunks();
        BitSet b = GlobalConstants.PEERLIST.get(peerId).getChunks();

        Boolean interested = false;

        if (!a.equals(b)) {
            a.and(b);
            interested = a.equals(Peer.currentPeer.getChunks());
        }

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
        log.info("received interested message from" + Integer.toString(peerId) + ",current peer port: " + Peer.currentPeer.getHostPort());
    }

    private void handleNotInterestedMessage(ActualMessage message) {
        log.info("received not interested message from" + Integer.toString(peerId) + ",current peer port: " + Peer.currentPeer.getHostPort());
    }

    private synchronized void sendRequestMessage() {
//        System.out.println(GlobalConstants.PEERLIST.get(peerId).getState());
        if (GlobalConstants.PEERLIST.get(peerId).isUnchockedForCurrentPeers()) {
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

    private void handleUnchokeMessage(ActualMessage message) {
        log.info("received unchoke message from" + Integer.toString(peerId) + ",current peer port: " + Peer.currentPeer.getHostPort());
        GlobalConstants.PEERLIST.get(peerId).setUnchockedForCurrentPeers(true);
        sendRequestMessage();

    }

    private void handleRequestMessage(ActualMessage message) {
        int chunkid = ByteBuffer.wrap(message.getMessage()).getInt();
        log.info("received Request message from" + Integer.toString(peerId) + ",current peer port: " + Peer.currentPeer.getHostPort() + "for bit" + ByteBuffer.wrap(message.getMessage()).getInt());
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
        for (int i = 0; i < chunk.length; i++) {
            if (i < 4) {
                chunkIdArr[i] = chunk[i];
            } else {
                fileChunk[i - 4] = chunk[i];
            }
        }
        int chunkId = ByteBuffer.wrap(chunkIdArr).getInt();

        log.info("received chunkId" + Integer.toString(chunkId) + ",current peer port: " + Peer.currentPeer.getHostPort());
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
