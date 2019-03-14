/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent.beans;

import bittorrent.PeerHandler;

/**
 *
 * @author harsh
 *
 * This file details the info listed inside the PeerInfo.cfg file.
 *
 * The file specifies description about the following properties:
 *
 * peerID - Acts as identifier for the peer joining the protocol and
 * participating in file exchange
 *
 * hostName - Name of the host machine
 *
 * hostPort - specifies host port number
 *
 * haveFile - flag indicating if chosen peer has the file.
 *
 * fileName - File chosen to exchange as part of bittorrent protocol
 *
 * Additionally, getter setters have been added to fetch, and set values to these properties
 *
 *
 */
public class PeerInfoConfigObject {

    int peerID;
    String hostName;
    int hostPort;
    boolean haveFile;
    PeerHandler peerHandler = null;

    // get the peerHandler
    public PeerHandler getPeerHandler() {
        return peerHandler;
    }

    // get the peerId
    public int getPeerID() {
        return peerID;
    }

    // get the hostName
    public String getHostName() {
        return hostName;
    }

    // get the hostPort
    public int getHostPort() {
        return hostPort;
    }

    // check if haveFile is true or false
    public boolean isHaveFile() {
        return haveFile;
    }

    // set the peerId
    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    // set the peerHandler
    public void setPeerHandler(PeerHandler peerHandler) {
        this.peerHandler = peerHandler;
    }

    // setter for hostname
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    // setter for hostPort
    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    // setter for haveFile
    public void setHaveFile(boolean haveFile) {
        this.haveFile = haveFile;
    }

}
