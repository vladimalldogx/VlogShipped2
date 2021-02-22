package com.uc.vlogshippedclient;

import android.app.Application;

import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SponsorActivity extends Application {

    private static APIInterface apiInterface;
    private static RealmConfiguration config;
    private static final String DEBUG_NEW = "http://192.168.43.251/";

    public static final String BASE_URL = DEBUG_NEW + "api/";
    public class ServiceType {
        public static final String REGISTER = "user/register";
        public static final String LOGIN = "user/login";
        public static final String EDITPROFILE = "user/editinfosponsor";
        public static final String GETCAMNPAIGN = "get/campaign";
        public static final String GETINFLUENCER = "get/influencer";
        public static final String GETRECOMMENDED = "get/influencerrecommend";
        public static final String GETINFLUENCERBYCATEGORY = "get/influencerbycategory";
        public static final String GETCONTENTBYCATEGORY = "get/contentbycategory";
        public static final String GETCONTENT = "get/content";
        public static final String ADDRATING = "add/rating";
        public static final String EDITAPPLICATION = "influencer/editapplication";
        public static final String ADDSUBSCRIPTION = "add/subscription";
        public static final String GETSUBSCRIPTION = "get/subscription";
        public static final String GETSUBSCRIPTIONHISTORY = "get/subscriptionhistory";
        public static final String FOLLOW = "influencer/follow";
        public static final String GETFOLLOW = "influencer/getFollow";
        public static final String DELETEFOLLOW = "influencer/deletefollow";
        public static final String CHAT = "add/chat";
        public static final String GETCHAT = "get/chat";
        public static final String GETCHATPROFILE = "get/chatprofile";
        public static final String UPDATECHAT = "update/chat";
        public static final String NOTIFICATION = "add/notification";
        public static final String UPDATENOTIFICATION = "update/notification";
        public static final String NOTIFICATIONDETAILS = "get/notificationDetails";
        public static final String GETSUBSCRIPTIONRATE = "get/subscriptionrate";


    }

    public class Sponsor {
        public static final String ADDCAMPAIGN = "sponsor/addcampaign";
        public static final String ADDSAMPLING = "sponsor/addsampling";
        public static final String UPDATECAMPAIGN = "sponsor/updatecampaign";
        public static final String DELETECAMPAIGN = "sponsor/deletecampaign";
    }

    public class Params {
        public static final String STATUS = "status";
        public static final String RESPONSE = "response";
    }

    public static int campaignID;
    public static String campaignTitle;
    public static String campaignDate;
    public static String campaignLink;
    public static String campaignPhoto;
    public static String campaignDescription;
    public static String campaignStartDate;
    public static String campaignEndDate;
    public static String campaignStartTime;
    public static String campaignEndTime;
    public static String campaignCategory;
    public static String campaignPriceRange;


    public static int influencerID;
    public static String influencerFirstName;
    public static String influencerLastName;
    public static String influencerEmailAddress;
    public static String influencerBirthday;
    public static String influencerProfile;
    public static String influencerRateAverage;
    public static String influencerWebsite;

    public static String contentDescription, contentPhotoUrl;
    public static int contentId;

    public static final String clientId = "ARbA0zDVLu1ZIJZtTkfqNAUTiAtPRDDvvBOh-WfBL71HQV8_adMQjlpCqUh3bAfTnkD3RwTvMfF2QVKY";

    @Override
    public void onCreate() {
        super.onCreate();


        init();
    }

    private void init() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Realm.init(this);
        config = new RealmConfiguration.Builder()
                .name("library.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    public static APIInterface getApiInterface() {
        if (apiInterface != null) {
            return apiInterface;
        } else {
            throw new IllegalStateException("Api interface not intialized.");
        }
    }

    public static Realm getRealm() {

        return Realm.getInstance(config);
    }

}
