package com.uc.vlogshippedclient.model;

public class Sampling {

    int id;
    int user_id;
    String title;
    String start_date;
    String start_time;
    String end_date;
    String end_time;
    String about_product;
    String photo_url;
    String requirements;
    String category;

    public Sampling(int id, int user_id, String title, String start_date, String start_time, String end_date,
                    String end_time, String about_product, String photo_url, String requirements, String category) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.start_date = start_date;
        this.start_time = start_time;
        this.end_date = end_date;
        this.end_time = end_time;
        this.about_product = about_product;
        this.photo_url = photo_url;
        this.requirements = requirements;
        this.category = category;
    }
}
