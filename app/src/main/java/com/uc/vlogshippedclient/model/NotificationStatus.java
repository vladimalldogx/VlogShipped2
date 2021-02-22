package com.uc.vlogshippedclient.model;

public class NotificationStatus {

    public String NotificationStatus;
    public String NotificationID;
    public String NotificationTypeID;
    public String NotificationFrom;


    public NotificationStatus(String notificationStatus, String notificationID, String notificationTypeID, String notificationFrom) {
        NotificationStatus = notificationStatus;
        NotificationID = notificationID;
        NotificationTypeID = notificationTypeID;
        NotificationFrom = notificationFrom;
    }
}
