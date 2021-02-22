package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.NotificationStatus;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationInviteActivity extends AppCompatActivity {

    private TextView tvFullName, tvCategory, tvCompanyName, tvWebsite;
    private CircleImageView profilePicture;

    private APIInterface apiInterface;

    private ProgressDialog dialog;

    private DatabaseReference sendStatus;
    private FirebaseDatabase firebaseDatabase;
    private NotificationStatus notificationStatus;


    private String notification_id, notification_from, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_invite);

        setupviews();
    }

    private void setupviews() {

        tvFullName = findViewById(R.id.full_name);
        tvCategory = findViewById(R.id.category);
        tvCompanyName = findViewById(R.id.tv_company);
        profilePicture = findViewById(R.id.profile_picture);
        tvWebsite = findViewById(R.id.tv_website);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        dialog = ProgressDialog.show(NotificationInviteActivity.this, "",
                "Please wait...", true);



        Bundle extras = getIntent().getExtras();

        notification_id = extras.getString("notification_id");
        notification_from = extras.getString("notification_from");
        key = extras.getString("key");

        details();

    }

    private void details() {
        Call<ResponseBody> call = apiInterface.notificationdetails(Integer.parseInt(notification_from), 0);
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
                                tvCategory.setText(influencerInfo.getString("category"));
                                tvWebsite.setText(influencerInfo.getString("website"));
                                Glide.with(NotificationInviteActivity.this).load(influencerInfo.getString("profile_picture")).into(profilePicture);

                                dialog.dismiss();

                            }else{
                                dialog.dismiss();
                                Toast.makeText(NotificationInviteActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(NotificationInviteActivity.this, ""+responseData, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(NotificationInviteActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void btnChat(View v){
        Intent intent = new Intent();
        intent.setClass(NotificationInviteActivity.this, ChatActivity.class);
        intent.putExtra("notification_from", notification_from);
        intent.putExtra("chat_id", "0");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
//

        firebaseDatabase = FirebaseDatabase.getInstance();
        sendStatus = firebaseDatabase.getReference("Notification").child(String.valueOf(UserDb.getUserAccount().getId())).child(key);
        sendStatus.removeValue();
    }

    public void btnBack(View v){

        firebaseDatabase = FirebaseDatabase.getInstance();
        sendStatus = firebaseDatabase.getReference("Notification").child(String.valueOf(UserDb.getUserAccount().getId())).child(key);
        sendStatus.removeValue();

        Intent intent = new Intent();
        intent.setClass(NotificationInviteActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
