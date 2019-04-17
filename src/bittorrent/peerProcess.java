/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

/**
 *
 *
 */
public class peerProcess {

    /**
     * @param args the command line arguments
     *
     * Marks the entry point of the BitTorrent project Parses the entry from the
     * command line indicating the ID of the newly formed peer
     *
     */
    public static void main(String[] args) {
        Peer peer;
        if (args.length > 0) {
            peer = new Peer(Integer.parseInt(args[0]));
            peer.loadPeer();
        } else {
            System.exit(0);
        }

    }

}
