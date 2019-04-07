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
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 *
 *
 */
public class Peer {

    static ScheduledExecutorService scheduler;
    public static int currentPeerID;
    public static PeerInfoConfigObject currentPeer;
    public static CommonConfigObject commonConfig;
    ArrayList<PeerInfoConfigObject> peerInfo;
    Logger log;

    Peer(int id) {
        currentPeerID = id;
        scheduler = Executors.newScheduledThreadPool(2);
    }

    // load the peer
    void loadPeer() {
        ConfLoader confLoader = new ConfLoader();
        String currentDir = System.getProperty("user.dir");
        GlobalConstants.log = UtilityHandlers.getLogger(currentPeerID, currentDir);
        createPeerFolder();
        log = GlobalConstants.log;
        commonConfig = confLoader.readCommonConfig();
        GlobalConstants.commonConfig = commonConfig;
        peerInfo = confLoader.readPeerInfoConfig();
        ListIterator<PeerInfoConfigObject> iterator = peerInfo.listIterator();
        if (Peer.commonConfig != null) {
                    long fileSize = Peer.commonConfig.getFileSize();
                    long pieceSize = Peer.commonConfig.getPieceSize();
                    long numSplits = fileSize / pieceSize;
                    long remainingBytes = fileSize % pieceSize;
                    if (remainingBytes > 0) {
                        numSplits = numSplits + 1;
                    }
                    GlobalConstants.chunkCount = numSplits;
        while (iterator.hasNext()) {
            PeerInfoConfigObject peer = iterator.next();
            if (peer.getPeerID() != currentPeerID) {
                GlobalConstants.expectedMessage.put(peer.getPeerID(), GlobalConstants.HANDSHAKE);
                if (peer.isHaveFile()) {
                        peer.getChunks().set(0, new Long(numSplits).intValue(), true);
                    } else {
                        peer.setChunks(new BitSet((int) numSplits));
                    }
                GlobalConstants.PEERLIST.put(peer.getPeerID(), peer);
                System.out.print(peer.getChunks());
                
            } else {
                currentPeer = peer;
                    if (peer.isHaveFile()) {
                        peer.getChunks().set(0, new Long(numSplits).intValue(), true);
                        String chunkPath = currentDir + File.separator + commonConfig.getFileName();
                        try {
                            FileUtility.splitFile(chunkPath, commonConfig.getPieceSize());
                        } catch (IOException ex) {
                            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        peer.setChunks(new BitSet((int) numSplits));
                    }
                }
            }
        }
        Server TCPserver = new Server(currentPeer);
        TCPserver.start();

        scheduler.scheduleAtFixedRate(new ChokeUnchokeHandler(), 3, commonConfig.getUnchokingInterval(), TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new OptimisticallyUnchokeHandler(), 3, commonConfig.getOptimisticUnchokingInterval(), TimeUnit.SECONDS);

        iterator = peerInfo.listIterator();
        while (iterator.hasNext()) {
            PeerInfoConfigObject peer = iterator.next();
            if (peer.getPeerID() != currentPeerID) {
                UtilityHandlers.sendTCPRequest(peer);
            } else {
                break;
            }
        }

    }

    // creates the peer folder
    public void createPeerFolder() {
        String currentDir = System.getProperty("user.dir");
        GlobalConstants.chunkDirectory=currentDir + File.separator + "peer_" + currentPeerID;
        File f = new File(GlobalConstants.chunkDirectory);
        if (!f.exists() && !f.isDirectory()) {
            f.mkdirs();
        }
        FileUtility.dir = f.getPath();
        System.out.print(FileUtility.dir);

    }
}
