/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent.beans;

/**
 *
 * @author harsh
 */
public class ActualMessage {
    int length;
    byte messageType;
    byte [] message;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
