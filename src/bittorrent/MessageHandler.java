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
    Logger log;
    
    MessageHandler(HandshakeObject handshakeObject) {
        this.handshakeObject = handshakeObject;
        this.log = GlobalConstants.log;
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
                    
                }
            }
        }
    }
    
    private void handleBitfieldMessage(ActualMessage message) {
        System.out.print("bitfield message received" + message.getLength());
        GlobalConstants.PEERLIST.get(handshakeObject.getPeerID()).setChunks(BitSet.valueOf(message.getMessage()));
        log.info("received bitfield message from peer"+handshakeObject.getPeerID()+"--"+GlobalConstants.PEERLIST.get(handshakeObject.getPeerID()).getChunks());
        log.info("current peer port: "+Peer.currentPeer.getHostPort()+"my bitfield:"+Peer.currentPeer.getChunks());
    }
}
