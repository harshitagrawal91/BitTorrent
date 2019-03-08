/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.GlobalConstants;
import bittorrent.beans.HandshakeObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 *
 * @author harsh
 */
public class PeerHandler extends Thread {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    public String nextExpectedMessage;
    Logger log;

    public PeerHandler(Socket socket, ObjectOutputStream o, ObjectInputStream i) {
        this.socket = socket;
        log = GlobalConstants.log;
        out = o;
        in = i;
    }

    public void run() {
        try {
            nextExpectedMessage = GlobalConstants.HANDSHAKE;
            log.info("peer" + Peer.peerID + "_input and output stream created");
            boolean check = false;
            try {
                while (true) {
                    switch (nextExpectedMessage) {
                        case GlobalConstants.HANDSHAKE:
                            HandshakeObject message = (HandshakeObject) in.readObject();
                            if (message!=null && message.getHeader().equals(GlobalConstants.HANDSHAKEHEADER)) {
                                if (GlobalConstants.PEERLIST.containsKey(message.getPeerID())) {
                                    GlobalConstants.PEERLIST.get(message.getPeerID()).setPeerHandler(this);
                                    GlobalConstants.expectedMessage.put(message.getPeerID(), GlobalConstants.BITFIELD);
                                    log.info("peer" + Peer.peerID + "_received handshake message from peer" + message.getPeerID());
                                    HandshakeObject handshake = new HandshakeObject();
                                    handshake.setPeerID(Peer.peerID);
                                    out.writeObject(handshake);
                                    out.flush();
                                    GlobalConstants.expectedMessage.put(message.getPeerID(), GlobalConstants.BITFIELD);
                                    nextExpectedMessage=GlobalConstants.expectedMessage.get(message.getPeerID());
                                } else {
                                    check = true;
                                    break;
                                }
                            } else {
                                check = true;
                                break;
                            }
                            break;
                    }
                    if (check == true) {
                        break;
                    }
                }
            } catch (ClassNotFoundException classnot) {
                log.info("Data received in unknown format");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // closing resources 
                this.in.close();
                this.out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMessage(Object msg) {
        try {
            out.writeObject(msg);
            out.flush();
//			System.out.println("Send message: " + msg + " to Client " + no);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
