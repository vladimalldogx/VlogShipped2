package com.uc.vlogshippedclient.model;

public class SubscriptionHistory {

    int amount;
    int payment_type;
    String created;
    String expiry_day;
    String payment_id;

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getExpiry_day() {
        return expiry_day;
    }

    public void setExpiry_day(String expiry_day) {
        this.expiry_day = expiry_day;
    }
}
