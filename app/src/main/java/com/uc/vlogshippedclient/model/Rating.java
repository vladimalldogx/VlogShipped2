package com.uc.vlogshippedclient.model;

public class Rating {
    int rating_from;
    int rating_to;
    int rate;
    int content_id;

    public Rating(int rating_from, int rating_to, int rate, int content_id) {
        this.rating_from = rating_from;
        this.rating_to = rating_to;
        this.rate = rate;
        this.content_id = content_id;
    }
}
