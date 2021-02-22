package com.uc.vlogshippedclient.model;

public class Campaign {

    int id;
    int user_id;
    String title;
    String start_date;
    String start_time;
    String end_date;
    String end_time;
    String product_url;
    String photo_url;
    String description;
    String category;
    String price_range;

    public Campaign(int id, int user_id, String title, String start_date, String start_time, String end_date, String end_time, String product_url, String photo_url, String description, String category, String price_range) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.start_date = start_date;
        this.start_time = start_time;
        this.end_date = end_date;
        this.end_time = end_time;
        this.product_url = product_url;
        this.photo_url = photo_url;
        this.description = description;
        this.category = category;
        this.price_range = price_range;
    }
}
