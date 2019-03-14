/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent.beans;

/**
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

    // Sets the number of prefered neighbours
    public void setNumberOfPreferedNeighbour(int numberOfPreferedNeighbour) {
        this.numberOfPreferedNeighbour = numberOfPreferedNeighbour;
    }

    // Sets the unchoking interval
    public void setUnchokingInterval(int unchokingInterval) {
        this.unchokingInterval = unchokingInterval;
    }

    // Sets the optimistic unchoking interval
    public void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
    }

    // Sets the filename
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Sets the filesize
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    // Sets the piece size
    public void setPieceSize(long pieceSize) {
        this.pieceSize = pieceSize;
    }

    // gets the number of prefered neighbour
    public int getNumberOfPreferedNeighbour() {
        return numberOfPreferedNeighbour;
    }

    // gets the number of unchoking interval
    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    // gets the number of optimistic unchoking interval
    public int getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }

    // get the filename
    public String getFileName() {
        return fileName;
    }

    // get the file size
    public long getFileSize() {
        return fileSize;
    }

    // get the piece size
    public long getPieceSize() {
        return pieceSize;
    }
    
}
