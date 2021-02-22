package com.uc.vlogshippedclient.util;

import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.model.Campaign;
import com.uc.vlogshippedclient.model.Content;
import com.uc.vlogshippedclient.model.EditUser;
import com.uc.vlogshippedclient.model.Followers;
import com.uc.vlogshippedclient.model.MyCampaign;
import com.uc.vlogshippedclient.model.NotifcationModel;
import com.uc.vlogshippedclient.model.Rating;
import com.uc.vlogshippedclient.model.Register;
import com.uc.vlogshippedclient.model.Sampling;
import com.uc.vlogshippedclient.model.Subscription;
import com.uc.vlogshippedclient.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @POST(SponsorActivity.ServiceType.REGISTER)
    Call<ResponseBody> register(@Body Register form);

    @POST(SponsorActivity.ServiceType.LOGIN)
    Call<ResponseBody> login(@Body User form);

    @POST(SponsorActivity.ServiceType.EDITPROFILE)
    Call<ResponseBody> editinfo(@Body EditUser form);

    @POST(SponsorActivity.Sponsor.ADDCAMPAIGN)
    Call<ResponseBody> addcampaign(@Body Campaign form);

    @POST(SponsorActivity.Sponsor.ADDSAMPLING)
    Call<ResponseBody> addsampling(@Body Sampling form);

    @POST(SponsorActivity.ServiceType.GETCAMNPAIGN)
    Call<ResponseBody> getcampaign(@Body MyCampaign form);

    @POST(SponsorActivity.Sponsor.UPDATECAMPAIGN)
    Call<ResponseBody> updatecampaign(@Body Campaign form);

    @POST(SponsorActivity.Sponsor.DELETECAMPAIGN)
    Call<ResponseBody> deletecampaign(@Body MyCampaign form);

    @GET(SponsorActivity.ServiceType.GETINFLUENCER)
    Call<ResponseBody> getinfluencer();

    @GET(SponsorActivity.ServiceType.GETRECOMMENDED)
    Call<ResponseBody> getrecommended();

    @POST(SponsorActivity.ServiceType.FOLLOW)
    Call<ResponseBody> follow(@Body Followers form);

    @POST(SponsorActivity.ServiceType.GETFOLLOW)
    Call<ResponseBody> getfollow(@Query("sponsor_id") int sponsor_id, @Query("influencer_id") int influencer_id);

    @POST(SponsorActivity.ServiceType.DELETEFOLLOW)
    Call<ResponseBody> deletefollow(@Query("sponsor_id") int sponsor_id, @Query("influencer_id") int influencer_id);

    @POST(SponsorActivity.ServiceType.GETINFLUENCERBYCATEGORY)
    Call<ResponseBody> getinfluencerbycategory(@Query("category") String category, @Query("sponsor_id") int sponsor_id);

    @POST(SponsorActivity.ServiceType.GETCONTENTBYCATEGORY)
    Call<ResponseBody> getcontentbycategory(@Query("category") String category, @Query("user_id") int user_id);

    @POST(SponsorActivity.ServiceType.GETCONTENT)
    Call<ResponseBody> getcontent(@Body Content form);

    @POST(SponsorActivity.ServiceType.ADDRATING)
    Call<ResponseBody> addrating(@Body Rating form);

    @POST(SponsorActivity.ServiceType.ADDSUBSCRIPTION)
    Call<ResponseBody> addsubcription(@Body Subscription form);

    @POST(SponsorActivity.ServiceType.GETSUBSCRIPTION)
    Call<ResponseBody> getsubscription(@Query("user_id") int user_id);

    @GET(SponsorActivity.ServiceType.GETSUBSCRIPTIONRATE)
    Call<ResponseBody> getsubscriptionrate();

    @POST(SponsorActivity.ServiceType.GETSUBSCRIPTIONHISTORY)
    Call<ResponseBody> getsubscriptionhistory(@Query("user_id") int user_id);

    @POST(SponsorActivity.ServiceType.NOTIFICATIONDETAILS)
    Call<ResponseBody> notificationdetails(@Query("notification_from") int user_id, @Query("campaign_id") int campaign_id);

    @POST(SponsorActivity.ServiceType.CHAT)
    Call<ResponseBody> addchat(@Query("chat_to") int chat_to, @Query("chat_from") int chat_from, @Query("chat_message") String chat_message);

    @POST(SponsorActivity.ServiceType.GETCHAT)
    Call<ResponseBody> getchat(@Query("user_id") int user_id);

    @POST(SponsorActivity.ServiceType.GETCHATPROFILE)
    Call<ResponseBody> getchatprofile(@Query("user_id") int user_id);

    @POST(SponsorActivity.ServiceType.UPDATECHAT)
    Call<ResponseBody> updatechat(@Query("chat_id") int chat_id, @Query("chat_message") String chat_message);

    @POST(SponsorActivity.ServiceType.NOTIFICATION)
    Call<ResponseBody> addnotification(@Body NotifcationModel form);

    @POST(SponsorActivity.ServiceType.UPDATENOTIFICATION)
    Call<ResponseBody> updatenotification(@Body NotifcationModel form, @Query("notification_id") int notification_id);

    @POST(SponsorActivity.ServiceType.EDITAPPLICATION)
    Call<ResponseBody> editapplication(@Query("application_status") int application_status, @Query("campaign_id") int campaign_id);

}
