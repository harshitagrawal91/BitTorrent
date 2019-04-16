/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;
import bittorrent.beans.GlobalConstants;
import bittorrent.beans.PeerInfoConfigObject;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author debdeepbasu
 */
public class Terminator implements Runnable{
    public void run() {
        try {
            if (Peer.currentPeer.getChunks().cardinality() != GlobalConstants.chunkCount) return;
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
                Peer.scheduler.shutdown();
                Peer.scheduler.awaitTermination(5, TimeUnit.SECONDS);
                for (int pid : GlobalConstants.PEERLIST.keySet()) {
                    PeerInfoConfigObject peer = GlobalConstants.PEERLIST.get(pid);
                    peer.getPeerHandler().stopPeer();
                }
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
