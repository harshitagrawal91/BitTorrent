/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.PeerInfoConfigObject;
import bittorrent.beans.CommonConfigObject;
import bittorrent.beans.GlobalConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.logging.Logger;

/**
 *
 * @author harsh
 */
public class Peer {

    public static int peerID;
    PeerInfoConfigObject serverInfo;
    CommonConfigObject commonConfig;
    ArrayList<PeerInfoConfigObject> peerInfo;
    Logger log;
    Peer(int id) {
        peerID = id;
    }

    void loadPeer() {
        ConfLoader confLoader = new ConfLoader();
        String currentDir = System.getProperty("user.dir");
        GlobalConstants.log = UtilityHandlers.getLogger(peerID, currentDir);
        log=GlobalConstants.log;
        commonConfig = confLoader.readCommonConfig();
        peerInfo = confLoader.readPeerInfoConfig();
        ListIterator<PeerInfoConfigObject> iterator = peerInfo.listIterator();
        while (iterator.hasNext()) {
             PeerInfoConfigObject peer = iterator.next();
            if(peer.getPeerID()!=peerID){
            GlobalConstants.expectedMessage.put(peer.getPeerID(), GlobalConstants.HANDSHAKE);
            GlobalConstants.PEERLIST.put(peer.getPeerID(), peer);
            }else{
                serverInfo=peer;
            }
        }
        Server TCPserver=new Server(serverInfo);
        TCPserver.start();
        iterator=peerInfo.listIterator();
        while (iterator.hasNext()){
            PeerInfoConfigObject peer = iterator.next();
            if(peer.getPeerID()!=peerID){
                UtilityHandlers.sendTCPRequest(peer);
            }else{
                break;
            }
        }
        log.info(commonConfig.getFileName());
        log.info(peerInfo.size() + " size");
        createPeerFolder();
    }

    public void createPeerFolder() {
        String currentDir = System.getProperty("user.dir");
        File f = new File(currentDir + File.separator + "peer_" + peerID);
        if (!f.exists() && !f.isDirectory()) {
            f.mkdirs();
        }

    }
}
