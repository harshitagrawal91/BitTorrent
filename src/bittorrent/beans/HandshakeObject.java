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
 *
 * This file provides implementation details for the handshake/orientation
 * between interating peers.
 * It lists out the following properties -
 *
 * header - a pre-set header, that reads - "P2PFILESHARINGPROJ"
 *
 * zeroBits - acts as a parity checker
 *
 * peerID - the ID of the peer participating in handshake
 *
 * The class contains getter, setter methods that assign and fetch values for each of these
 * properties respectively.
 *
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
