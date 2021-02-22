package com.uc.vlogshippedclient.model;

public class NotifcationModel {

    public int notification_status;
    public int notification_type_id;
    public int notification_from;
    public int notification_to;

    public NotifcationModel(int notification_status, int notification_type_id, int notification_from, int notification_to) {
        this.notification_status = notification_status;
        this.notification_type_id = notification_type_id;
        this.notification_from = notification_from;
        this.notification_to = notification_to;
    }
}
