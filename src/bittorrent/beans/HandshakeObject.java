/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent.beans;

import java.io.Serializable;
import java.util.BitSet;

/**
 *
 * @author harsh
 */
public class HandshakeObject implements Serializable{
    final String header="P2PFILESHARINGPROJ";
    BitSet zeroBits=new BitSet(80);
    int peerID;

    public BitSet getZeroBits() {
        return zeroBits;
    }

    public void setZeroBits(BitSet zeroBits) {
        this.zeroBits = zeroBits;
    }

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public String getHeader() {
        return header;
    }
    
}
