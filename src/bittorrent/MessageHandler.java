/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.ActualMessage;
import bittorrent.beans.HandshakeObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import bittorrent.beans.GlobalConstants;
import java.util.BitSet;
import java.util.logging.Logger;
/**
 *
 * @author harsh
 */
public class MessageHandler extends Thread {
    
    private HandshakeObject handshakeObject = null;
    private PeerHandler peerHandler = null;
    Logger log;
    
    MessageHandler(PeerHandler peerHandler, HandshakeObject handshakeObject) {
        this.handshakeObject = handshakeObject;
        this.log = GlobalConstants.log;
        this.peerHandler = peerHandler;
    }
    
    
    public ConcurrentLinkedQueue<ActualMessage> 
            messageQueue = new ConcurrentLinkedQueue<ActualMessage>(); 
    
    public void run(){
        while(true){
            if(!messageQueue.isEmpty()){
                ActualMessage message = messageQueue.poll();
                if (message.getMessageType()==GlobalConstants.messageType.BITFIELD.getValue()) {
                    handleBitfieldMessage(message);
                } else if (message.getMessageType() == GlobalConstants.messageType.CHOKE.getValue()) {
                    
                } else if (message.getMessageType() == GlobalConstants.messageType.UNCHOKE.getValue()) {
                    
                } else if (message.getMessageType() == GlobalConstants.messageType.INTERESTED.getValue()) {
                    handleInterestedMessage(message);
                }
            }
        }
    }
    
    private void handleBitfieldMessage(ActualMessage message) {
        System.out.print("bitfield message received" + message.getLength());
        GlobalConstants.PEERLIST.get(handshakeObject.getPeerID()).setChunks(BitSet.valueOf(message.getMessage()));
        log.info("received bitfield message from peer"+handshakeObject.getPeerID()+"--"+GlobalConstants.PEERLIST.get(handshakeObject.getPeerID()).getChunks());
        log.info("current peer port: "+Peer.currentPeer.getHostPort()+"my bitfield:"+Peer.currentPeer.getChunks());
        if (!Peer.currentPeer.getChunks().isEmpty()) {
            ActualMessage responseBitfieldMessage = new ActualMessage();
            responseBitfieldMessage.setMessageType(GlobalConstants.messageType.BITFIELD.getValue());
            responseBitfieldMessage.setMessage(Peer.currentPeer.getChunks().toByteArray());
            responseBitfieldMessage.setLength(responseBitfieldMessage.getMessage().length+1);
            peerHandler.sendMessage(responseBitfieldMessage);
        }
        BitSet a = Peer.currentPeer.getChunks();
        BitSet b = GlobalConstants.PEERLIST.get(handshakeObject.getPeerID()).getChunks();
        Boolean interested = false;
        if (!a.equals(b)) {
            a.and(b);
            interested = a.equals(Peer.currentPeer.getChunks());
        }
        if (interested) {
            log.info("current peer port: "+Peer.currentPeer.getHostPort()+"is interested!");
            ActualMessage interestedMessage = new ActualMessage();
            interestedMessage.setMessageType(GlobalConstants.messageType.INTERESTED.getValue());
            interestedMessage.setLength(1);
            peerHandler.sendMessage(interestedMessage);
        }
        
    }
    
    private void handleInterestedMessage(ActualMessage message) {
        log.info("received interested message from"+Integer.toString(handshakeObject.getPeerID())+",current peer port: "+Peer.currentPeer.getHostPort());
    }
}
