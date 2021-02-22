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
import com.uc.vlogshippedclient.adapter.SubscriptionHistoryAdapter;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.MyCampaign;
import com.uc.vlogshippedclient.model.SubscriptionHistory;
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

public class SubscriptionHistoryActivity extends AppCompatActivity {

    private RecyclerView rvSubscriptionHistory;
    private List<SubscriptionHistory> historyList = new ArrayList<>();
    private SubscriptionHistory subscriptionHistory;
    private SubscriptionHistoryAdapter subscriptionHistoryAdapter;

    private APIInterface apiInterface;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_history);


        setupviews();
    }

    private void setupviews() {

        rvSubscriptionHistory = findViewById(R.id.rv_subs_history);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        dialog = ProgressDialog.show(SubscriptionHistoryActivity.this, "",
                "Getting Campaign/s, Please wait...", true);

        getHistory();

        subscriptionHistoryAdapter = new SubscriptionHistoryAdapter(this, historyList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvSubscriptionHistory.setLayoutManager(mLayoutManager);
        rvSubscriptionHistory.setItemAnimator(new DefaultItemAnimator());
        rvSubscriptionHistory.setAdapter(subscriptionHistoryAdapter);

    }

    private void getHistory() {

        Call<ResponseBody> call = apiInterface.getsubscriptionhistory(10);
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

                                            SubscriptionHistory history = new SubscriptionHistory();
                                            history.setAmount(bookLists.getInt("amount"));
                                            history.setPayment_type(bookLists.getInt("payment_type"));
                                            history.setCreated(bookLists.getString("created"));
                                            history.setExpiry_day(bookLists.getString("expiry_day"));
                                            history.setPayment_id(bookLists.getString("payment_id"));

                                            historyList.add(history);

                                        }
                                    }
                                    dialog.dismiss();
                                    Log.i("History", ""+bookList);
                                    subscriptionHistoryAdapter.notifyDataSetChanged();
                                }else{
                                    dialog.dismiss();
                                    Toast.makeText(SubscriptionHistoryActivity.this, ""+bookList, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                dialog.dismiss();
                                Toast.makeText(SubscriptionHistoryActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SubscriptionHistoryActivity.this, ""+responseData, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(SubscriptionHistoryActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnBack(View v){
        finish();
    }
}
