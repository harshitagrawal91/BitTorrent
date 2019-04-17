/*
 * 
 * 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 * 
 */
package bittorrent;

import bittorrent.beans.GlobalConstants;
import bittorrent.beans.PeerInfoConfigObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.logging.Logger;

/**
 *
 *
 *
 *
 */
public class Server extends Thread {

    PeerInfoConfigObject serverPeer;
    private ServerSocket listener = null;
    Logger log;

    /**
     *
     *
     * sets logging and identification details for each instance of the Server
     * class
     *
     */
    public Server(PeerInfoConfigObject serverPeer) {
        this.serverPeer = serverPeer;
        log = GlobalConstants.log;
    }

    /**
     *
     *
     * Creates a new instance of the ServerSocket class and logs this event
     * providing the peer ID and port number of the created object.
     *
     *
     */
    public void run() {
        try {
            listener = new ServerSocket(serverPeer.getHostPort());
            log.info("Peer " + serverPeer.getPeerID() + "_Server started at port " + serverPeer.getHostPort());
            while (true) {
                Socket socket = null;
                socket = listener.accept();
                if (socket != null) {
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    new PeerHandler(socket, out, in).start();
                }
            }
        } catch (IOException e) {
        } finally {
            try {
                listener.close();
            } catch (IOException e) {
            }
        }
    }
}
