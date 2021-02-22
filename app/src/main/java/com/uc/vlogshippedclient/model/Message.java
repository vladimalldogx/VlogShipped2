package com.uc.vlogshippedclient.model;

public class Message {

    public String chatMessage;
    public String chatID;
    public String chatTo;
    public String chatFrom;


    public Message(String chatMessage, String chatID, String chatTo, String chatFrom) {
        this.chatMessage = chatMessage;
        this.chatID = chatID;
        this.chatTo = chatTo;
        this.chatFrom = chatFrom;
    }
}
