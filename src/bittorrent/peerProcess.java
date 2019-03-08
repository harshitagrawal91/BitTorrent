/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

/**
 *
 * @author harsh
 */
public class peerProcess {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Peer peer;
        System.out.println("perr");
        if (args.length > 0) {
            peer = new Peer(Integer.parseInt(args[0]));
            peer.loadPeer();
        } else {
            System.out.println("invalid arguments");
            System.exit(0);
        }

    }

}
