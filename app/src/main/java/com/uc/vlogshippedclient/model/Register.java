package com.uc.vlogshippedclient.model;

public class Register {
    String first_name;
    String last_name;
    String mobile_number;
    String email_address;
    String birthday;
    String password;
    String gender;
    String company_name;
    int user_type;
    int user_status;
    String website;
    String description;
    String profile_picture;
    String category;


    public Register(String first_name, String last_name,
                    String mobile_number, String email_address, String birthday, String password, int user_type, int user_status ,String gender, String company_name,
                    String website, String description, String category, String profile_picture) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.mobile_number = mobile_number;
        this.email_address = email_address;
        this.password = password;
        this.user_type = user_type;
        this.user_status = user_status;
        this.gender = gender;
        this.company_name = company_name;
        this.website = website;
        this.description = description;
        this.category = category;
        this.profile_picture = profile_picture;
    }
}
