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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author harsh
 */
public class ChokeUnchokeHandler implements Runnable {

    public void run() {
        ConcurrentHashMap<Integer, PeerInfoConfigObject> interestedPeers = GlobalConstants.interestedPeers;
        ArrayList<PeerInfoConfigObject> sortedInterestedPeer = new ArrayList();
        if (!interestedPeers.isEmpty()) {
            for (int peerid : interestedPeers.keySet()) {
                PeerInfoConfigObject temp = interestedPeers.get(peerid);
                if (temp.getState() == GlobalConstants.messageType.UNCHOKE.getValue() && temp.isOptimisticallyUnchoke() == false) {
                    temp.setState(GlobalConstants.messageType.CHOKE.getValue());
                    ActualMessage chokeMessage = new ActualMessage();
                    chokeMessage.setLength(1);
                    chokeMessage.setMessageType(GlobalConstants.messageType.CHOKE.getValue());
                    temp.getPeerHandler().sendMessage(chokeMessage);
                }
                sortedInterestedPeer.add(interestedPeers.get(peerid));
            }

            Collections.sort(sortedInterestedPeer, (a, b) -> (int) (b.getDownloadSpeed() - a.getDownloadSpeed()));
            int count = 0;
            while (!sortedInterestedPeer.isEmpty() && count < GlobalConstants.commonConfig.getNumberOfPreferedNeighbour()) {
                PeerInfoConfigObject peer = sortedInterestedPeer.get(0);
                if (peer.getState() != GlobalConstants.messageType.UNCHOKE.getValue()) {
                    ActualMessage unchokeMessage = new ActualMessage();
                    unchokeMessage.setLength(1);
                    unchokeMessage.setMessageType(GlobalConstants.messageType.UNCHOKE.getValue());
                    peer.setState(GlobalConstants.messageType.UNCHOKE.getValue());
                    peer.getPeerHandler().sendMessage(unchokeMessage);
                    count++;
                }
                sortedInterestedPeer.remove(0);
            }
        }

//        System.out.print("choke unchoke");
    }
}
