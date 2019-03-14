/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent;

import bittorrent.beans.PeerInfoConfigObject;
import bittorrent.beans.CommonConfigObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 */
public class ConfLoader {

    public CommonConfigObject readCommonConfig() {
        CommonConfigObject conf = new CommonConfigObject();
        File f = new File("common.cfg");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                String s[] = line.split("\\s");
                switch (s[0].toLowerCase()) {
                    case "numberofpreferredneighbors":
                        conf.setNumberOfPreferedNeighbour(Integer.parseInt(s[1]));
                        break;
                    case "unchokinginterval":
                        conf.setUnchokingInterval(Integer.parseInt(s[1]));
                        break;
                    case "optimisticunchokinginterval":
                        conf.setOptimisticUnchokingInterval(Integer.parseInt(s[1]));
                        break;
                    case "filename":
                        conf.setFileName(s[1]);
                        break;
                    case "filesize":
                        conf.setFileSize(Long.parseLong(s[1]));
                        break;
                    case "piecesize":
                        conf.setPieceSize(Long.parseLong(s[1]));
                        break;
                    default:
                        System.out.println("Invalig Configuration File");
                        return null;
                }
            }
        } catch (FileNotFoundException fe) {
            System.out.print("common config file not found" + fe);
        } catch (IOException e) {
            System.out.print("IO exception while reading common config" + e);
        }
        return conf;
    }

    // read in peer info config
    public ArrayList<PeerInfoConfigObject> readPeerInfoConfig() {
        ArrayList<PeerInfoConfigObject> peerarr = new ArrayList<>();
        File f = new File("PeerInfo.cfg");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                String s[] = line.split("\\s");
                PeerInfoConfigObject peer = new PeerInfoConfigObject();
                peer.setPeerID(Integer.parseInt(s[0]));
                peer.setHostName(s[1]);
                peer.setHostPort(Integer.parseInt(s[2]));
                if (Integer.parseInt(s[3]) == 1) {
                    peer.setHaveFile(true);
                } else if (Integer.parseInt(s[3]) == 0) {
                    peer.setHaveFile(false);
                }
                peerarr.add(peer);
            }
        } catch (FileNotFoundException fe) {
            System.out.print("peerinfo config file not found" + fe);
        } catch (IOException e) {
            System.out.print("IO exception while reading peerinfo config" + e);
        }
        return peerarr;
    }
}
