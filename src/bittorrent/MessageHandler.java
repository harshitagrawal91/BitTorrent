/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.ActualMessage;
import java.util.concurrent.ConcurrentLinkedQueue;

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
                System.out.print("message received"+messageQueue.poll().getLength());
            }
        }
    }
}
