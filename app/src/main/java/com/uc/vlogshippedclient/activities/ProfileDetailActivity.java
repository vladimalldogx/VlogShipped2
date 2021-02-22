package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.adapter.CategoryAdapter;
import com.uc.vlogshippedclient.adapter.ContentAdapter;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Content;
import com.uc.vlogshippedclient.model.Followers;
import com.uc.vlogshippedclient.model.Influencer;
import com.uc.vlogshippedclient.model.NotifcationModel;
import com.uc.vlogshippedclient.model.NotificationStatus;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDetailActivity extends AppCompatActivity {

    private TextView tvFullName, tvCategory, tvAddress, tvRate, tvWebsite;
    private RecyclerView rvContent, rvCategories;
    private RatingBar rateBar;
    private Button btnFollow;

    private List<Content> contentListing = new ArrayList<>();
    private APIInterface apiInterface;
    private ProgressDialog dialogContent, dialogFollow;
    private CircleImageView profilePicture;

    private ContentAdapter contentAdapter;
    private CategoryAdapter categoryAdapter;

    private DatabaseReference sendStatus;
    private FirebaseDatabase firebaseDatabase;
    private NotificationStatus notificationStatus;

    private int layoutPosition = 0;

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        setupviews();


    }

    private void setupviews() {
        tvFullName = findViewById(R.id.full_name);
        tvCategory = findViewById(R.id.category);
        tvAddress = findViewById(R.id.address);
        rvContent = findViewById(R.id.rv_content);
        rvCategories = findViewById(R.id.rv_categories);
        profilePicture = findViewById(R.id.profile_picture);
        tvRate = findViewById(R.id.tv_rate);
        tvWebsite = findViewById(R.id.tv_website);
        rateBar = findViewById(R.id.rate);
        btnFollow = findViewById(R.id.btn_follow);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        dialogContent = ProgressDialog.show(ProfileDetailActivity.this, "",
                "Getting Content, Please wait...", true);

        categoryAdapter = new CategoryAdapter(ProfileDetailActivity.this, 6, layoutPosition);
        RecyclerView.LayoutManager categorylayout = new LinearLayoutManager(ProfileDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(categorylayout);
        rvCategories.setItemAnimator(new DefaultItemAnimator());
        rvCategories.setAdapter(categoryAdapter);



        contentAdapter = new ContentAdapter(this, contentListing);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContent.setLayoutManager(mLayoutManager);
        rvContent.setItemAnimator(new DefaultItemAnimator());
        rvContent.setAdapter(contentAdapter);

        tvFullName.setText(SponsorActivity.influencerFirstName.toUpperCase()+" "+SponsorActivity.influencerLastName.toUpperCase());

        tvCategory.setText(SponsorActivity.influencerEmailAddress);

        tvAddress.setText(SponsorActivity.influencerBirthday);

        tvRate.setText(SponsorActivity.influencerRateAverage);

        tvWebsite.setText(SponsorActivity.influencerWebsite);

        Glide.with(this).load(SponsorActivity.influencerProfile).into(profilePicture);

        rateBar.setRating(Float.parseFloat(SponsorActivity.influencerRateAverage));
        
        getContent();



    }

    private void getContent() {
        Call<ResponseBody> call = apiInterface.getcontent(new Content(SponsorActivity.influencerID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONArray bookList = jsonResponseObject.getJSONArray(SponsorActivity.Params.RESPONSE);
                                if(bookList.length()>0){
                                    for (int i=0;i<bookList.length();i++){
                                        JSONObject bookLists = bookList.getJSONObject(i);
                                        if(bookLists.length()>0){
                                            Content myContent = new Content(SponsorActivity.influencerID);
                                            myContent.setId(bookLists.getInt("id"));
                                            myContent.setUrl(bookLists.getString("url"));
                                            myContent.setDescription(bookLists.getString("description"));
                                            myContent.setUpdated_at(bookLists.getString("updated_at"));
                                            myContent.setTitle(bookLists.getString("title"));
                                            myContent.setCategory(bookLists.getString("category"));
                                            myContent.setThumbnail(bookLists.getString("thumbnail"));
                                            contentListing.add(myContent);

                                        }
                                    }
                                    Log.i("String", ""+bookList);
                                    contentAdapter.notifyDataSetChanged();
                                    getfollow();
                                }else{
                                    //dialogContent.dismiss();
                                    getfollow();
                                }
                            }
                        }catch (Exception e){
                            dialogContent.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogContent.dismiss();
                        Log.e("Error", ""+e);
                    }

                }else{
                    dialogContent.dismiss();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogContent.dismiss();
                Toast.makeText(ProfileDetailActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getfollow() {

        Call<ResponseBody> call = apiInterface.getfollow(UserDb.getUserAccount().getId(), SponsorActivity.influencerID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONArray bookList = jsonResponseObject.getJSONArray(SponsorActivity.Params.RESPONSE);
                                if(bookList.length()>0){

                                    btnFollow.setText("UNFOLLOW");
                                    counter++;
                                    dialogContent.dismiss();

                                }else{
                                    dialogContent.dismiss();
                                }
                            }
                        }catch (Exception e){
                            dialogContent.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogContent.dismiss();
                        Log.e("Error", ""+e);
                    }

                }else{
                    dialogContent.dismiss();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogContent.dismiss();
                Toast.makeText(ProfileDetailActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            layoutPosition = Integer.parseInt(intent.getStringExtra("layoutposition"));
            String category = intent.getStringExtra("category");
            contentbycategory(category, SponsorActivity.influencerID);
            //Toast.makeText(InfluencerListActivity.this,category,Toast.LENGTH_SHORT).show();

        }
    };

    private void contentbycategory(String category, int influencerID) {

        contentListing.clear();

        Call<ResponseBody> call = apiInterface.getcontentbycategory(category, influencerID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONArray bookList = jsonResponseObject.getJSONArray(SponsorActivity.Params.RESPONSE);
                                if(bookList.length()>0){
                                    for (int i=0;i<bookList.length();i++){
                                        JSONObject bookLists = bookList.getJSONObject(i);
                                        if(bookLists.length()>0){
                                            Content myContent = new Content(SponsorActivity.influencerID);
                                            myContent.setId(bookLists.getInt("id"));
                                            myContent.setUrl(bookLists.getString("url"));
                                            myContent.setDescription(bookLists.getString("description"));
                                            myContent.setUpdated_at(bookLists.getString("updated_at"));
                                            myContent.setTitle(bookLists.getString("title"));
                                            myContent.setCategory(bookLists.getString("category"));
                                            myContent.setThumbnail(bookLists.getString("thumbnail"));

                                            contentListing.add(myContent);

                                        }
                                        else{
                                            dialogContent.dismiss();
                                            contentAdapter.notifyDataSetChanged();
                                            categoryAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    Log.i("String", ""+bookList);
                                    dialogContent.dismiss();
                                    contentAdapter.notifyDataSetChanged();
                                    categoryAdapter.notifyDataSetChanged();
                                    getfollow();

                                }else{
                                    dialogContent.dismiss();
                                    contentAdapter.notifyDataSetChanged();
                                    categoryAdapter.notifyDataSetChanged();
                                }
                            }
                        }catch (Exception e){
                            dialogContent.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogContent.dismiss();
                        Log.e("Error", ""+e);
                    }

                }else{
                    dialogContent.dismiss();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogContent.dismiss();
                Toast.makeText(ProfileDetailActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void follow (View v){

        if(counter == 0){
            dialogFollow = ProgressDialog.show(ProfileDetailActivity.this, "",
                    "Following, Please wait...", true);


            Call<ResponseBody> call = apiInterface.follow(new Followers(UserDb.getUserAccount().getId(), SponsorActivity.influencerID));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                    if(responseData.isSuccessful()){
                        try {
                            String stringResponse = responseData.body().string();
                            try {
                                JSONObject jsonResponseObject = new JSONObject(stringResponse);
                                if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                    dialogFollow.dismiss();
                                    Toast.makeText(ProfileDetailActivity.this, "Successfully Followed!", Toast.LENGTH_SHORT).show();
                                    counter++;
                                    btnFollow.setText("UNFOLLOW");

                                }else{
                                    Toast.makeText(ProfileDetailActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                                    dialogFollow.dismiss();
                                }
                            }catch (Exception e){
                                dialogFollow.dismiss();
                                Log.e("Error", ""+e);
                            }
                        }catch (Exception e){
                            dialogFollow.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }else{
                        Toast.makeText(ProfileDetailActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                        dialogFollow.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialogFollow.dismiss();
                    Log.i("Response Error:", ""+t);
                }
            });
        }else{
            dialogFollow = ProgressDialog.show(ProfileDetailActivity.this, "",
                    "Unfollowing, Please wait...", true);


            Call<ResponseBody> call = apiInterface.deletefollow(UserDb.getUserAccount().getId(), SponsorActivity.influencerID);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                    if(responseData.isSuccessful()){
                        try {
                            String stringResponse = responseData.body().string();
                            try {
                                JSONObject jsonResponseObject = new JSONObject(stringResponse);
                                if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                    dialogFollow.dismiss();
                                    counter = 0;
                                    Toast.makeText(ProfileDetailActivity.this, "Successfully UnFollowed!", Toast.LENGTH_SHORT).show();
                                    btnFollow.setText("FOLLOW");

                                }else{
                                    Toast.makeText(ProfileDetailActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                                    dialogFollow.dismiss();
                                }
                            }catch (Exception e){
                                dialogFollow.dismiss();
                                Log.e("Error", ""+e);
                            }
                        }catch (Exception e){
                            dialogFollow.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }else{
                        Toast.makeText(ProfileDetailActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                        dialogFollow.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialogFollow.dismiss();
                    Log.i("Response Error:", ""+t);
                }
            });
        }

    }

    public void email (View v){

        Log.i("Send email", "");
        String[] TO = {SponsorActivity.influencerEmailAddress};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email.", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ProfileDetailActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

    }

    public void btnInvite(View v){

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileDetailActivity.this);

        alertDialogBuilder.setMessage("Are you sure you want to collaborate with this Influencer?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog2, int which) {
                dialogContent = ProgressDialog.show(ProfileDetailActivity.this, "",
                        "Sending Invite, Please wait...", true);
                
                
                addnotification();

                dialog2.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                dialog1.dismiss();
            }
        });

        alertDialogBuilder.show();



    }

    private void addnotification() {

        Call<ResponseBody> call = apiInterface.addnotification(new NotifcationModel(0, 4, UserDb.getUserAccount().getId(), SponsorActivity.influencerID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                JSONObject response = jsonResponseObject.getJSONObject(SponsorActivity.Params.RESPONSE);

                                firebaseDatabase = FirebaseDatabase.getInstance();
                                sendStatus = firebaseDatabase.getReference("Notification").child(String.valueOf(SponsorActivity.influencerID));
                                notificationStatus = new NotificationStatus("1", response.getString("notification_id"),
                                        "4", String.valueOf(UserDb.getUserAccount().getId()));
                                sendStatus.push().setValue(notificationStatus);

                                Toast.makeText(ProfileDetailActivity.this, "Invitation Sent", Toast.LENGTH_SHORT).show();

                                finish();

                                dialogContent.dismiss();



                            }else{
                                Toast.makeText(ProfileDetailActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                                dialogContent.dismiss();
                            }
                        }catch (Exception e){
                            dialogContent.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogContent.dismiss();
                        Log.e("Error", ""+e);
                    }
                }else{
                    Toast.makeText(ProfileDetailActivity.this, "Please check your internet Connection "+responseData, Toast.LENGTH_SHORT).show();
                    dialogContent.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileDetailActivity.this, "No Internet Connection"+t, Toast.LENGTH_SHORT).show();
                dialogContent.dismiss();
            }
        });
    }

    public void btnBack(View v){
        finish();
    }


}
