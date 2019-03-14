/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent.beans;

/**
 *
 * @author harsh
 *
 * This file details the info listed inside the common.cfg file.
 *
 * The file specifies description about the following properties:
 *
 * Number of preferred neighbours - The number of neighbours chosen peer decides to scan
 * for sharing file fragments
 *
 * unchoking interval -
 *
 * optimisticUnchokingInterval -
 *
 * fileName - File chosen to exchange as part of bittorrent protocol
 *
 * fileSize - The size of total file in bytes
 *
 * pieceSize - The file fragment chosen to be exchanged between the interacting peers
 *
 *
 */
public class CommonConfigObject {
    int numberOfPreferedNeighbour;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String fileName;
    long fileSize;
    long pieceSize;

    public void setNumberOfPreferedNeighbour(int numberOfPreferedNeighbour) {
        this.numberOfPreferedNeighbour = numberOfPreferedNeighbour;
    }

    public void setUnchokingInterval(int unchokingInterval) {
        this.unchokingInterval = unchokingInterval;
    }

    public void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setPieceSize(long pieceSize) {
        this.pieceSize = pieceSize;
    }

    public int getNumberOfPreferedNeighbour() {
        return numberOfPreferedNeighbour;
    }

    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    public int getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getPieceSize() {
        return pieceSize;
    }
    
}
