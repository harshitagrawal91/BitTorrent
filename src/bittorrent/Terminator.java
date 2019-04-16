/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;
import bittorrent.beans.GlobalConstants;
import bittorrent.beans.PeerInfoConfigObject;

/**
 *
 * @author debdeepbasu
 */
public class Terminator implements Runnable{
    public void run() {
        while (true) {
            if (Peer.currentPeer.getChunks().cardinality() != GlobalConstants.chunkCount) continue;
            int count = 0;
            GlobalConstants.log.info("terminator running");
            GlobalConstants.log.info("chunkCount:" + GlobalConstants.chunkCount );
            for (Integer pid : GlobalConstants.PEERLIST.keySet()) {
                PeerInfoConfigObject peer = GlobalConstants.PEERLIST.get(pid);
                GlobalConstants.log.info("peer-id: " + pid + " cardinality->" + peer.getChunks().cardinality());
                if (peer.getChunks().cardinality() == GlobalConstants.chunkCount) count++;
            }
            GlobalConstants.log.info("terminator count: " + count);
            GlobalConstants.log.info("total count" + GlobalConstants.PEERLIST.keySet().size());

            if (count == GlobalConstants.PEERLIST.keySet().size()) {
                System.exit(0);
            }
        }
    }
}
