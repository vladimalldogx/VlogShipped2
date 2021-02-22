package com.uc.vlogshippedclient.model;

public class Subscription {

    public String payment_id;
    public String payment_status;
    public int user_id;
    int amount;
    int payment_type;

    public Subscription(String payment_id, String payment_status, int user_id, int amount, int payment_type) {
        this.payment_id = payment_id;
        this.payment_status = payment_status;
        this.user_id = user_id;
        this.amount = amount;
        this.payment_type = payment_type;
    }

}
