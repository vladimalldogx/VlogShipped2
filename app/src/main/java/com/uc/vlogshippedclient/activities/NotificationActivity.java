package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.NotifcationModel;
import com.uc.vlogshippedclient.model.NotificationStatus;
import com.uc.vlogshippedclient.model.SubscriptionHistory;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private TextView tvFullName, tvCategory, tvBirthday, tvRate, tvWebsite, tvTitle, tvDate;
    private CircleImageView profilePicture;
    private RatingBar rateBar;

    private ImageView campaignPicture;

    private APIInterface apiInterface;
    private ProgressDialog dialog, dialogAccept;

    private DatabaseReference sendStatus;
    private FirebaseDatabase firebaseDatabase;

    private String notification_id, campaign_id, notification_from, key;

    private NotificationStatus notificationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setupviews();
    }

    private void setupviews() {

        tvFullName = findViewById(R.id.full_name);
        tvCategory = findViewById(R.id.category);
        tvBirthday = findViewById(R.id.tv_birthday);
        profilePicture = findViewById(R.id.profile_picture);
        tvRate = findViewById(R.id.tv_rate);
        tvWebsite = findViewById(R.id.tv_website);
        rateBar = findViewById(R.id.rate);

        campaignPicture = findViewById(R.id.img_picture);
        tvTitle = findViewById(R.id.tv_title);
        tvDate = findViewById(R.id.tv_date);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        dialog = ProgressDialog.show(NotificationActivity.this, "",
                "Please wait...", true);


        Bundle extras = getIntent().getExtras();

         notification_id = extras.getString("notification_id");
         campaign_id = extras.getString("campaign_id");
         notification_from = extras.getString("notification_from");
         key = extras.getString("key");


        details();

    }

    private void details() {

        Call<ResponseBody> call = apiInterface.notificationdetails(Integer.parseInt(notification_from), Integer.parseInt(campaign_id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONObject influencerInfo = jsonResponseObject.getJSONObject(SponsorActivity.Params.RESPONSE);
                                tvFullName.setText(influencerInfo.getString("first_name")+" "+influencerInfo.getString("last_name"));
                                tvBirthday.setText(influencerInfo.getString("birthday"));
                                tvCategory.setText(influencerInfo.getString("category"));
                                tvWebsite.setText(influencerInfo.getString("website"));
                                Glide.with(NotificationActivity.this).load(influencerInfo.getString("profile_picture")).into(profilePicture);
                                rateBar.setRating(Float.parseFloat(influencerInfo.getString("rate_average")));
                                tvRate.setText(influencerInfo.getString("rate_average"));

                                JSONObject campaignInfo = influencerInfo.getJSONObject("campaign");

                                tvTitle.setText(campaignInfo.getString("title"));
                                tvDate.setText(campaignInfo.getString("start_date")+ " " + campaignInfo.getString("start_time")
                                +" "+campaignInfo.getString("end_date")+" "+campaignInfo.getString("end_time"));

                                Glide.with(NotificationActivity.this).load(campaignInfo.getString("photo_url")).into(campaignPicture);


                                dialog.dismiss();

                            }else{
                                dialog.dismiss();
                                Toast.makeText(NotificationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            dialog.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialog.dismiss();
                        Log.e("Error", ""+e);
                    }

                }else{
                    dialog.dismiss();
                    Toast.makeText(NotificationActivity.this, ""+responseData, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(NotificationActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void btnAccept (View v){

        dialogAccept = ProgressDialog.show(NotificationActivity.this, "",
                "Accepting, Please wait...", true);


        updatenotification(0);

    }

    private void updatenotification(int status) {
        Call<ResponseBody> call = apiInterface.updatenotification(new NotifcationModel(status, Integer.parseInt(campaign_id), UserDb.getUserAccount().getId(), Integer.parseInt(notification_from)), Integer.parseInt(notification_id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                //JSONObject response = jsonResponseObject.getJSONObject(SponsorActivity.Params.RESPONSE);


                                process(1);


                            }else{
                                Toast.makeText(NotificationActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                                dialogAccept.dismiss();
                            }
                        }catch (Exception e){
                            dialogAccept.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogAccept.dismiss();
                        Log.e("Error", ""+e);
                    }
                }else{
                    Toast.makeText(NotificationActivity.this, "Please check your internet Connection "+responseData, Toast.LENGTH_SHORT).show();
                    dialogAccept.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "No Internet Connection"+t, Toast.LENGTH_SHORT).show();
                dialogAccept.dismiss();
            }
        });
    }

    public void btnDecline (View v){

        dialogAccept = ProgressDialog.show(NotificationActivity.this, "",
                "Declining, Please wait...", true);

        updatenotification(6);

        process(0);

    }

    private void process(final int i) {

        Call<ResponseBody> call = apiInterface.editapplication(2, Integer.parseInt(campaign_id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                dialogAccept.dismiss();

                                firebaseDatabase = FirebaseDatabase.getInstance();
                                sendStatus = firebaseDatabase.getReference("Notification").child(String.valueOf(UserDb.getUserAccount().getId())).child(key);
                                notificationStatus = new NotificationStatus("0", notification_id,
                                        String.valueOf(campaign_id), notification_from);
                                sendStatus.setValue(notificationStatus);

                                if(i == 1){
                                    sendStatus = firebaseDatabase.getReference("Notification").child(String.valueOf(notification_from)).child(key);
                                    notificationStatus = new NotificationStatus("0", notification_id,
                                            String.valueOf(campaign_id), String.valueOf(UserDb.getUserAccount().getId()));
                                    sendStatus.setValue(notificationStatus);
                                }


                                Toast.makeText(NotificationActivity.this, "Successfully Accepted. Please wait for Approval!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(NotificationActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }else{
                                Toast.makeText(NotificationActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                                dialogAccept.dismiss();
                            }
                        }catch (Exception e){
                            dialogAccept.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogAccept.dismiss();
                        Log.e("Error", ""+e);
                    }
                }else{
                    Toast.makeText(NotificationActivity.this, "Please check your internet Connection "+responseData, Toast.LENGTH_SHORT).show();
                    dialogAccept.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "No Internet Connection"+t, Toast.LENGTH_SHORT).show();
                dialogAccept.dismiss();
            }
        });
    }

    public void btnBack (View v){

        firebaseDatabase = FirebaseDatabase.getInstance();
        sendStatus = firebaseDatabase.getReference("Notification").child(String.valueOf(UserDb.getUserAccount().getId())).child(key);
        sendStatus.removeValue();

        Intent intent = new Intent();
        intent.setClass(NotificationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
