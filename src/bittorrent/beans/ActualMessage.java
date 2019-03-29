/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bittorrent.beans;

import java.io.Serializable;

/**
 *
 * This file details the below properties regarding the messages exchanged between the peers
 * engaged in file transfer -
 *
 * Length - the size of the message
 *
 * Type - the type of the message providing details about it's objective
 *
 * message - the actual message body
 *
 * The class contains getter, setter methods that assign and fetch values for each of these
 * properties respectively.
 *
 */
public class ActualMessage implements Serializable {
    int length;
    byte messageType;
    byte [] message;

    // get the length
    public int getLength() {
        return length;
    }

    // setter for length
    public void setLength(int length) {
        this.length = length;
    }

    // getter for messageType
    public byte getMessageType() {
        return messageType;
    }

    // setter for messageType
    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    // getter for message
    public byte[] getMessage() {
        return message;
    }

    // setter for message
    public void setMessage(byte[] message) {
        this.message = message;
    }
}
