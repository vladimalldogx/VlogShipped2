package com.uc.vlogshippedclient.model;

public class EditUser {

    int user_id;
    String first_name;
    String last_name;
    String mobile_number;
    String website;
    String company_name;
    String description;
    String profile_picture;


    public EditUser(int user_id, String first_name, String last_name,
                    String company_name, String description, String mobile_number, String website, String profile_picture) {

        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.description = description;
        this.mobile_number = mobile_number;
        this.company_name = company_name;
        this.description = description;
        this.website = website;
        this.profile_picture = profile_picture;

    }
}
