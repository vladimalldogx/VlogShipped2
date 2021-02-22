package com.uc.vlogshippedclient.model;

public class ChatHistory {

    String chat_id;
    String chat_to;
    String chat_from;
    String chat_message;

    public String getChat_message() {
        return chat_message;
    }

    public void setChat_message(String chat_message) {
        this.chat_message = chat_message;
    }

    public String getChat_to() {
        return chat_to;
    }

    public void setChat_to(String chat_to) {
        this.chat_to = chat_to;
    }

    public String getChat_from() {
        return chat_from;
    }

    public void setChat_from(String chat_from) {
        this.chat_from = chat_from;
    }


    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

}
