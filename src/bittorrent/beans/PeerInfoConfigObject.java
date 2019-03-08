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
 */
public class PeerInfoConfigObject {

    int peerID;
    String hostName;
    int hostPort;
    boolean haveFile;
    PeerHandler peerHandler = null;

    public PeerHandler getPeerHandler() {
        return peerHandler;
    }

    public int getPeerID() {
        return peerID;
    }

    public String getHostName() {
        return hostName;
    }

    public int getHostPort() {
        return hostPort;
    }

    public boolean isHaveFile() {
        return haveFile;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public void setPeerHandler(PeerHandler peerHandler) {
        this.peerHandler = peerHandler;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public void setHaveFile(boolean haveFile) {
        this.haveFile = haveFile;
    }

}
