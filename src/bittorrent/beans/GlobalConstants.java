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
 * @author harsh
 */
public class GlobalConstants {
    public static HashMap <Integer,String> expectedMessage=new HashMap<>();
    public static HashMap <Integer,PeerInfoConfigObject> PEERLIST=new HashMap<>();
    public static final String HANDSHAKE="handshake"; 
    public static final String HANDSHAKEHEADER="P2PFILESHARINGPROJ";
    public static final String BITFIELD="bitfield";
    public static Logger log;
}
