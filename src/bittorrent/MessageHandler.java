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
    
   
    private int peerId;
    Logger log;
    
    MessageHandler(int peerId) {
        this.log = GlobalConstants.log;
        this.peerId = peerId;
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
                } else if (message.getMessageType() == GlobalConstants.messageType.NOT_INTERESTED.getValue()) {
                    handleNotInterestedMessage(message);
                }
                
            }else{
//                this.wait();
            }
        }
    }
    
    private void handleBitfieldMessage(ActualMessage message) {
        System.out.print("bitfield message received" + message.getLength());
        // TODO why do we have to set the peer's bitfield again, even when it was was set during handshake?
        GlobalConstants.PEERLIST.get(peerId).setChunks(BitSet.valueOf(message.getMessage()));
        
        log.info("received bitfield message from peer"+peerId+"--"+GlobalConstants.PEERLIST.get(peerId).getChunks());
        
        // send currentPeer's bitfield to peer if it's not empty
        if (!Peer.currentPeer.getChunks().isEmpty()) {
            ActualMessage responseBitfieldMessage = new ActualMessage();
            responseBitfieldMessage.setMessageType(GlobalConstants.messageType.BITFIELD.getValue());
            responseBitfieldMessage.setMessage(Peer.currentPeer.getChunks().toByteArray());
            responseBitfieldMessage.setLength(responseBitfieldMessage.getMessage().length+1);
            GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(responseBitfieldMessage);
        }
        
        // TODO: find out if you don't have a chunk which your peer has
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
        
        if (interested)
            interestedOrNotMessage.setMessageType(GlobalConstants.messageType.INTERESTED.getValue());
        else
            interestedOrNotMessage.setMessageType(GlobalConstants.messageType.NOT_INTERESTED.getValue());
        
        
        GlobalConstants.PEERLIST.get(peerId).getPeerHandler().sendMessage(interestedOrNotMessage);
        
    }
    
    private void handleInterestedMessage(ActualMessage message) {
        
        GlobalConstants.interestedPeers.put(peerId,GlobalConstants.PEERLIST.get(peerId) );
        log.info("received interested message from"+Integer.toString(peerId)+",current peer port: "+Peer.currentPeer.getHostPort());
    }
    
    private void handleNotInterestedMessage(ActualMessage message) {
        log.info("received not interested message from"+Integer.toString(peerId)+",current peer port: "+Peer.currentPeer.getHostPort());
    }
}
