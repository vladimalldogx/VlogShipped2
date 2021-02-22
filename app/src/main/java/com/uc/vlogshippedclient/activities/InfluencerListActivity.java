package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.adapter.CategoryAdapter;
import com.uc.vlogshippedclient.adapter.InfluencerListAdapter;
import com.uc.vlogshippedclient.adapter.RecommendedInfluencerAdapter;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Influencer;
import com.uc.vlogshippedclient.model.RecommendedInfluencer;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfluencerListActivity extends AppCompatActivity {

    private RecyclerView rvVlogger, rvCategory, rvRecommend;
    private InfluencerListAdapter influencerListAdapter;
    private CategoryAdapter categoryAdapter;
    private RecommendedInfluencerAdapter recommendedInfluencerAdapter;
    private LinearLayout recommendLayout;

    private APIInterface apiInterface;
    private ProgressDialog dialog;

    private List<Influencer> influencers = new ArrayList<>();
    private List<RecommendedInfluencer> recommendedInfluencersLists = new ArrayList<>();

    private int layoutPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influencer_list);

        setupviews();
    }

    private void setupviews() {
        rvVlogger = findViewById(R.id.rv_vlogger);
        rvCategory = findViewById(R.id.rv_categories);
        recommendLayout = findViewById(R.id.recommend_layout);
        rvRecommend = findViewById(R.id.rv_recommended);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        categoryAdapter = new CategoryAdapter(InfluencerListActivity.this, 6, layoutPosition);
        RecyclerView.LayoutManager categorylayout = new LinearLayoutManager(InfluencerListActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvCategory.setLayoutManager(categorylayout);
        rvCategory.setItemAnimator(new DefaultItemAnimator());
        rvCategory.setAdapter(categoryAdapter);

        recommendedInfluencerAdapter = new RecommendedInfluencerAdapter(InfluencerListActivity.this, recommendedInfluencersLists);
        RecyclerView.LayoutManager recommendedLayout = new LinearLayoutManager(InfluencerListActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvRecommend.setLayoutManager(recommendedLayout);
        rvRecommend.setItemAnimator(new DefaultItemAnimator());
        rvRecommend.setAdapter(recommendedInfluencerAdapter);


        influencerListAdapter = new InfluencerListAdapter(InfluencerListActivity.this,influencers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InfluencerListActivity.this, LinearLayoutManager.VERTICAL, false);
        rvVlogger.setLayoutManager(mLayoutManager);
        rvVlogger.setItemAnimator(new DefaultItemAnimator());
        rvVlogger.setAdapter(influencerListAdapter);

        dialog = ProgressDialog.show(InfluencerListActivity.this, "",
                "Getting Influencer/s, Please wait...", true);

        Call<ResponseBody> call = apiInterface.getinfluencer();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONArray influencerlist = jsonResponseObject.getJSONArray(SponsorActivity.Params.RESPONSE);
                                if(influencerlist.length()>0){
                                    for (int i=0;i<influencerlist.length();i++){
                                        JSONObject influencerlistings = influencerlist.getJSONObject(i);
                                        if(influencerlistings.length()>0){
                                            Influencer influencer = new Influencer();
                                            influencer.setId(influencerlistings.getInt("id"));
                                            influencer.setEmail_address(influencerlistings.getString("email_address"));
                                            influencer.setFirst_name(influencerlistings.getString("first_name"));
                                            influencer.setLast_name(influencerlistings.getString("last_name"));
                                            influencer.setGender(influencerlistings.getString("gender"));
                                            influencer.setMobile_number(influencerlistings.getString("mobile_number"));
                                            influencer.setBirthday(influencerlistings.getString("birthday"));
                                            influencer.setWebsite(influencerlistings.getString("website"));
                                            influencer.setProfile_picture(influencerlistings.getString("profile_picture"));
                                            influencer.setRate_average(influencerlistings.getString("rate_average"));

                                            influencers.add(influencer);

                                        }
                                    }
                                    Log.i("String", ""+influencerlist);
                                    influencerListAdapter.notifyDataSetChanged();
                                    recommended();
                                }else{
                                    dialog.dismiss();
                                }
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

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(InfluencerListActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void recommended() {

        Call<ResponseBody> call = apiInterface.getrecommended();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONArray influencerlist = jsonResponseObject.getJSONArray(SponsorActivity.Params.RESPONSE);
                                if(influencerlist.length()>0) {
                                    if (influencerlist.getString(0).compareToIgnoreCase("none") == 0) {
                                        dialog.dismiss();
                                    } else {
                                        for (int i = 0; i < influencerlist.length(); i++) {
                                            JSONObject influencerlistings = influencerlist.getJSONObject(i);
                                            if (influencerlistings.length() > 0) {
                                                RecommendedInfluencer recommendedInfluencer = new RecommendedInfluencer();
                                                recommendedInfluencer.setId(influencerlistings.getInt("id"));
                                                recommendedInfluencer.setEmail_address(influencerlistings.getString("email_address"));
                                                recommendedInfluencer.setFirst_name(influencerlistings.getString("first_name"));
                                                recommendedInfluencer.setLast_name(influencerlistings.getString("last_name"));
                                                recommendedInfluencer.setGender(influencerlistings.getString("gender"));
                                                recommendedInfluencer.setMobile_number(influencerlistings.getString("mobile_number"));
                                                recommendedInfluencer.setBirthday(influencerlistings.getString("birthday"));
                                                recommendedInfluencer.setProfile_picture(influencerlistings.getString("profile_picture"));
                                                recommendedInfluencer.setWebsite(influencerlistings.getString("website"));
                                                recommendedInfluencer.setRate_average(influencerlistings.getString("rate_average"));

                                                recommendedInfluencersLists.add(recommendedInfluencer);

                                            }
                                        }
                                        dialog.dismiss();
                                        recommendLayout.setVisibility(View.VISIBLE);
                                        Log.i("DebugInfo", "" + influencerlist.length());
                                        recommendedInfluencerAdapter.notifyDataSetChanged();
                                    }
                                }else{
                                    dialog.dismiss();
                                }
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

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(InfluencerListActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            layoutPosition = Integer.parseInt(intent.getStringExtra("layoutposition"));
            String category = intent.getStringExtra("category");
            influencerbycategory(category);
            //Toast.makeText(InfluencerListActivity.this,category,Toast.LENGTH_SHORT).show();

        }
    };

    private void influencerbycategory(final String category) {

        influencers.clear();

        Call<ResponseBody> call = apiInterface.getinfluencerbycategory(category, UserDb.getUserAccount().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONArray influencerlist = jsonResponseObject.getJSONArray(SponsorActivity.Params.RESPONSE);
                                if(influencerlist.length()>0){
                                    for (int i=0;i<influencerlist.length();i++){
                                        JSONObject influencerlistings = influencerlist.getJSONObject(i);
                                        if(influencerlistings.length()>0){
                                            Influencer influencer = new Influencer();
                                            influencer.setId(influencerlistings.getInt("id"));
                                            influencer.setEmail_address(influencerlistings.getString("email_address"));
                                            influencer.setFirst_name(influencerlistings.getString("first_name"));
                                            influencer.setLast_name(influencerlistings.getString("last_name"));
                                            influencer.setGender(influencerlistings.getString("gender"));
                                            influencer.setMobile_number(influencerlistings.getString("mobile_number"));
                                            influencer.setBirthday(influencerlistings.getString("birthday"));
                                            influencer.setProfile_picture(influencerlistings.getString("profile_picture"));
                                            influencer.setRate_average(influencerlistings.getString("rate_average"));

                                            influencers.add(influencer);

                                        }else{
                                            influencerListAdapter.notifyDataSetChanged();
                                            categoryAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    dialog.dismiss();
                                    Log.i("String", ""+influencerlist);
                                    influencerListAdapter.notifyDataSetChanged();
                                    categoryAdapter.notifyDataSetChanged();
                                }else{
                                    dialog.dismiss();
                                    influencerListAdapter.notifyDataSetChanged();
                                    categoryAdapter.notifyDataSetChanged();
                                }
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

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(InfluencerListActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });



    }
}
