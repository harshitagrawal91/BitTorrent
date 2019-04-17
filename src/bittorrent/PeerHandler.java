/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.ActualMessage;
import bittorrent.beans.GlobalConstants;
import bittorrent.beans.HandshakeObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

/**
 *
 */
public class PeerHandler extends Thread {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private int handlerForPeer;
    private MessageHandler messageHandler = null;
    public String nextExpectedMessage;
    Logger log;
    private volatile static boolean exit = false;

    public PeerHandler(Socket socket, ObjectOutputStream o, ObjectInputStream i) {
        this.socket = socket;
        log = GlobalConstants.log;
        out = o;
        in = i;
    }

    public void run() {
        try {
            nextExpectedMessage = GlobalConstants.HANDSHAKE;
            boolean check = false;
            try {
                while (!exit) {
                    if (in == null) {
                        continue;
                    }
                    Object obj = in.readObject();
                    if (obj == null) {
                        continue;
                    }
                    if (obj instanceof HandshakeObject) {
                        HandshakeObject message = (HandshakeObject) obj;
                        if (message != null && message.getHeader().equals(GlobalConstants.HANDSHAKEHEADER)) {
                            if (GlobalConstants.PEERLIST.containsKey(message.getPeerID())) {
                                GlobalConstants.PEERLIST.get(message.getPeerID()).setPeerHandler(this);
                                handlerForPeer = message.getPeerID();
                                log.info("peer " + Peer.currentPeerID + " is connected with peer " + message.getPeerID());
                                if (GlobalConstants.expectedMessage.get(message.getPeerID()) == GlobalConstants.HANDSHAKE) {
                                    HandshakeObject handshake = new HandshakeObject();
                                    handshake.setPeerID(Peer.currentPeerID);
                                    out.writeObject(handshake);
                                    out.flush();
                                }
                                if (!Peer.currentPeer.getChunks().isEmpty()) {
                                    ActualMessage bitfieldMessage = new ActualMessage();
                                    bitfieldMessage.setMessageType(GlobalConstants.messageType.BITFIELD.getValue());
                                    bitfieldMessage.setMessage(Peer.currentPeer.getChunks().toByteArray());
                                    bitfieldMessage.setLength(bitfieldMessage.getMessage().length + 1);
                                    out.writeObject(bitfieldMessage);
                                    out.flush();
                                }
                                if (messageHandler == null) {
                                    messageHandler = new MessageHandler(handlerForPeer);
                                    messageHandler.start();
                                }
                            } else {
                                check = true;
                                break;
                            }
                        }
                    } else if (obj instanceof ActualMessage) {
                        messageHandler.messageQueue.add((ActualMessage) obj);
                        if (messageHandler.getState().equals(Thread.State.WAITING)) {
                            synchronized (messageHandler) {
                                messageHandler.notify();
                            }
                        }
                    }
                    if (check == true) {
                        break;
                    }
                }
            } catch (ClassNotFoundException classnot) {
            } catch (SocketException s) {
                return;
            }
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                // closing resources 
                this.in.close();
                this.out.close();

            } catch (IOException e) {
//                e.printStackTrace();
            }
        }

    }

    public void stopPeer() {
        exit = true;
    }

    // method to send message
    public void sendMessage(Object msg) {
        try {
            if (out != null) {
                out.writeObject(msg);
                out.flush();
            }
//			System.out.println("Send message: " + msg + " to Client " + no);
        } catch (IOException ioException) {
//            ioException.printStackTrace();
        }
    }

}
