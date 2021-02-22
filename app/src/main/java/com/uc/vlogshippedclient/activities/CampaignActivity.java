package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.adapter.CampaignAdapter;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.MyCampaign;
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

public class CampaignActivity extends AppCompatActivity {

    private RecyclerView rvCampaign;

    private List<MyCampaign> campaignListing = new ArrayList<>();

    private APIInterface apiInterface;
    private ProgressDialog dialogCampaign;

    private MyCampaign myCampaign;
    private CampaignAdapter campaignAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        setupviews();
    }

    private void setupviews() {
        rvCampaign = findViewById(R.id.rv_campaign);


        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();


        dialogCampaign = ProgressDialog.show(CampaignActivity.this, "",
                "Getting Campaign/s, Please wait...", true);

        getCampaign();

        campaignAdapter = new CampaignAdapter(this, campaignListing);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvCampaign.setLayoutManager(mLayoutManager);
        rvCampaign.setItemAnimator(new DefaultItemAnimator());
        rvCampaign.setAdapter(campaignAdapter);

    }

    private void getCampaign() {

        Call<ResponseBody> call = apiInterface.getcampaign(new MyCampaign(UserDb.getUserAccount().getId(), 0));
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
                                            MyCampaign myCampaign = new MyCampaign(UserDb.getUserAccount().getId(), 0);
                                            myCampaign.setId(bookLists.getInt("id"));
                                            myCampaign.setTitle(bookLists.getString("title"));
                                            myCampaign.setStart_date(bookLists.getString("start_date"));
                                            myCampaign.setStart_time(bookLists.getString("start_time"));
                                            myCampaign.setEnd_date(bookLists.getString("end_date"));
                                            myCampaign.setEnd_time(bookLists.getString("end_time"));
                                            myCampaign.setProduct_url(bookLists.getString("product_url"));
                                            myCampaign.setPhoto_url(bookLists.getString("photo_url"));
                                            myCampaign.setDescription(bookLists.getString("description"));
                                            myCampaign.setCategory(bookLists.getString("category"));
                                            myCampaign.setPrice_range(bookLists.getString("price_range"));


                                            campaignListing.add(myCampaign);

                                        }
                                    }
                                    dialogCampaign.dismiss();
                                    Log.i("String", ""+bookList);
                                    campaignAdapter.notifyDataSetChanged();
                                }else{
                                    dialogCampaign.dismiss();
                                }
                            }
                        }catch (Exception e){
                            dialogCampaign.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogCampaign.dismiss();
                        Log.e("Error", ""+e);
                    }

                }else{
                    dialogCampaign.dismiss();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogCampaign.dismiss();
                Toast.makeText(CampaignActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnBack(View v){
        Intent i = new Intent(CampaignActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
