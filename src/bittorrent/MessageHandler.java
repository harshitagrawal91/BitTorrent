/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.ActualMessage;
import java.util.concurrent.ConcurrentLinkedQueue;
import bittorrent.beans.GlobalConstants;
/**
 *
 * @author harsh
 */
public class MessageHandler extends Thread {
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
    }
}
