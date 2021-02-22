package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Content;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewSubscriptionActivity extends AppCompatActivity {

    private TextView tvDays, tvPlans, tvAmount, tvType;
    private APIInterface apiInterface;
    private ProgressDialog dialog;
    private Button btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subscription);

        setupviews();
    }

    private void setupviews() {

        tvDays = findViewById(R.id.tv_days);
        tvPlans = findViewById(R.id.tv_plans);
        tvAmount = findViewById(R.id.tv_amount);
        tvType = findViewById(R.id.tv_type);
        btnHistory = findViewById(R.id.btn_history);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        dialog = ProgressDialog.show(ViewSubscriptionActivity.this, "",
                "Getting Subscription Details, Please wait...", true);

        getsubscription();


        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ViewSubscriptionActivity.this, SubscriptionHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getsubscription() {
        Call<ResponseBody> call = apiInterface.getsubscription(UserDb.getUserAccount().getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                tvDays.setText(""+jsonResponseObject.getJSONObject("response").getInt("difference"));
                                if(jsonResponseObject.getJSONObject("response").getInt("payment_type") == 1){
                                    tvPlans.setText("Pro Plan");
                                    tvAmount.setText("P4499.00");
                                    tvType.setText("Annual");
                                }else{
                                    tvPlans.setText("Starter Plan");
                                    tvAmount.setText("P999.00");
                                    tvType.setText("Monthly");
                                }
                                dialog.dismiss();
                            }else{
                                dialog.dismiss();
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
                Toast.makeText(ViewSubscriptionActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnBack(View v){
        finish();
    }
}
