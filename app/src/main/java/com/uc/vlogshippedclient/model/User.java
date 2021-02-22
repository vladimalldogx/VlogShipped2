package com.uc.vlogshippedclient.model;

public class User {

    String email_address;
    String password;
    int user_type;

    public User(String email_address, String password, int user_type) {
        this.email_address = email_address;
        this.password = password;
        this.user_type = user_type;
    }

}
