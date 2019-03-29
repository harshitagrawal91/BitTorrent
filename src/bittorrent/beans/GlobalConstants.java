/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent.beans;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * The file specifies description about the following globally used properties, variables:
 *
 * expectedMessage - describes out the expected log message to be printed when certain event occurs
 *
 * PEERLIST - details total number of interacting peers, sourced after reading PeerInfo.cfg
 *
 * log - object of the JAVA logging module, to print out the appropriate log message in the
 * log file of the chosen peer.
 *
 *
 *
 */
public class GlobalConstants {
    public static HashMap <Integer,String> expectedMessage=new HashMap<>();
    public static HashMap <Integer,PeerInfoConfigObject> PEERLIST=new HashMap<>();
    public static final String HANDSHAKE="handshake"; 
    public static final String HANDSHAKEHEADER="P2PFILESHARINGPROJ";
    public static final String BITFIELD="bitfield";
    public static Logger log;
    public static String filePath=null;
    public static enum messageType {
    CHOKE((byte)0),
    UNCHOKE((byte)1),
    BITFIELD((byte)5);
    private final byte value;

    private messageType(byte val)
    {
        this.value = val;
    }
    public byte getValue() {
    return value;
  }
    }
}
