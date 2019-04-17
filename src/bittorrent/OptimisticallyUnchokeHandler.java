/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.ActualMessage;
import bittorrent.beans.GlobalConstants;
import bittorrent.beans.PeerInfoConfigObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 *
 *
 */
public class OptimisticallyUnchokeHandler implements Runnable {

    Logger log;

    public void run() {
        ConcurrentHashMap<Integer, PeerInfoConfigObject> interestedPeers = GlobalConstants.interestedPeers;
        ArrayList<PeerInfoConfigObject> sortedInterestedPeer = new ArrayList();
        if (!interestedPeers.isEmpty()) {
            for (int peerid : interestedPeers.keySet()) {
                PeerInfoConfigObject temp = interestedPeers.get(peerid);
                if (temp.isOptimisticallyUnchoke() == true) {
                    temp.setState(GlobalConstants.messageType.CHOKE.getValue());
                    log.info("Peer " + Peer.currentPeer.getHostPort() + " has the optimistically unchoked neighbor " + Integer.toString(peerid));
                }

                sortedInterestedPeer.add(interestedPeers.get(peerid));
            }

            log.info("Peer " + Peer.currentPeer.getHostPort() + " has the preferred neighbors " + sortedInterestedPeer);

            Collections.sort(sortedInterestedPeer, (a, b) -> (int) (b.getDownloadSpeed() - a.getDownloadSpeed()));
            int k = GlobalConstants.commonConfig.getNumberOfPreferedNeighbour();
            if (sortedInterestedPeer.size() > k) {
                int randomInd;
                PeerInfoConfigObject randomPeer;
                while (true) {
                    randomInd = ThreadLocalRandom.current().nextInt(k, sortedInterestedPeer.size());
                    randomPeer = sortedInterestedPeer.get(randomInd);
                    if (randomPeer.getState() != GlobalConstants.messageType.UNCHOKE.getValue()) {
                        break;
                    }
                }
                if (sortedInterestedPeer.size() >= randomInd + 1) {
                    ActualMessage unchokeMessage = new ActualMessage();
                    unchokeMessage.setLength(1);
                    unchokeMessage.setMessageType(GlobalConstants.messageType.UNCHOKE.getValue());
                    randomPeer.getPeerHandler().sendMessage(unchokeMessage);
                }
            }
        }
    }

}
