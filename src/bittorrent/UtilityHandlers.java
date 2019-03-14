/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.HandshakeObject;
import bittorrent.beans.PeerInfoConfigObject;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.SimpleFormatter;

/**
 *
 * This file provides implementation details about -
 *
 * The logging format to be followed, initiation of the TCP Request
 *
 */
public class UtilityHandlers {

    /*

    This method details the logging format, creates a separate log file for
    a specific peer in it's designated file location.

     */

    public static Logger getLogger(int peerID, String currentDir) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tT]: %5$s %n");
        Logger logger = Logger.getLogger("peer_" + peerID);
        FileHandler fh;

        String path = currentDir +File.separator+ "log_peer_" + peerID + ".log";
        try {
            fh = new FileHandler(path, true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.warning("log check");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
    public static boolean sendTCPRequest(PeerInfoConfigObject remotePeer){

        /*

        This method initiates a new socket by taking in the host name and port no.
        It establishes a handshake after it's creation, with each of the previously
        existing peers

        */

        try{
        Socket socket = new Socket(remotePeer.getHostName(), remotePeer.getHostPort());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
	     out.flush();
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             HandshakeObject handshake=new HandshakeObject();
             handshake.setPeerID(Peer.peerID);
             out.writeObject(handshake);
	     out.flush();
             new PeerHandler(socket,out,in).start();
             
        }catch(IOException e){
            e.printStackTrace();
        }
        return true;
    }
}
