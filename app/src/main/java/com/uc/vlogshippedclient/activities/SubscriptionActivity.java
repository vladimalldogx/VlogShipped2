package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Register;
import com.uc.vlogshippedclient.model.Subscription;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 7171;

    private Button btnSubmit;
    private ProgressDialog dialogSubmit, dialog;
    private APIInterface apiInterface;
    private TextView tvStarterplan, tvProplan;

    private Register registerModel;

    private Intent i;

    private int amount = 999, monthly, annual;
    private int payment_type = 0;

    private LinearLayout monthlyLayout, annualLayout;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(SponsorActivity.clientId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        i = getIntent();

        setupviews();
    }

    private void setupviews() {
        btnSubmit = findViewById(R.id.btn_submit);
        monthlyLayout = findViewById(R.id.subs_layout_monthly);
        annualLayout = findViewById(R.id.subs_layout_annual);
        tvStarterplan = findViewById(R.id.starter_plan);
        tvProplan = findViewById(R.id.pro_plan);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        monthlyLayout.setBackground(getResources().getDrawable(R.color.colorAccent));
        annualLayout.setBackground(getResources().getDrawable(R.drawable.subs_box));


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSubmit = ProgressDialog.show(SubscriptionActivity.this, "",
                        "Registering, Please wait...", true);

                processpayment();

            }
        });

        dialog = ProgressDialog.show(SubscriptionActivity.this, "",
                "Please wait...", true);


        getsubsrate();


    }

    private void getsubsrate() {

        Call<ResponseBody> call = apiInterface.getsubscriptionrate();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                if(jsonResponseObject.getString(SponsorActivity.Params.RESPONSE) == null){

                                }else{
                                    monthly = jsonResponseObject.getJSONObject("response").getInt("monthly_rate");
                                    annual = jsonResponseObject.getJSONObject("response").getInt("annual_rate");
                                    tvStarterplan.setText("P"+jsonResponseObject.getJSONObject("response").getInt("monthly_rate"));
                                    tvProplan.setText("P"+jsonResponseObject.getJSONObject("response").getInt("annual_rate"));
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
                Toast.makeText(SubscriptionActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processpayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount), "PHP",
                "Vlogshipped subscription", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        Log.i("Paypal Error", "" + confirmation.toJSONObject().toString(4));
                        sendtodb(confirmation.toJSONObject().toString(4));
                    } catch (Exception e) {
                        Log.i("Paypal Error", "" + e);
                        dialogSubmit.dismiss();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
                dialogSubmit.dismiss();
            }
        }
    }

    private void sendtodb(final String details) {

        registerModel = new Register(i.getStringExtra("first_name"), i.getStringExtra("last_name"),
                i.getStringExtra("contact"), i.getStringExtra("email"),
                i.getStringExtra("birthday"), i.getStringExtra("password"), 0, 1,"male", i.getStringExtra("company"), i.getStringExtra("website"),
                "none", i.getStringExtra("category"), "none");

        Call<ResponseBody> call = apiInterface.register(registerModel);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if (responseData.isSuccessful()) {
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                sendtodb1(jsonResponseObject.getJSONObject("response").getInt("id"), details);

                            } else if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 401) {

                                dialogSubmit.dismiss();
                                Toast.makeText(SubscriptionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            } else if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 406) {

                                dialogSubmit.dismiss();
                                Toast.makeText(SubscriptionActivity.this, "Email Already Taken.", Toast.LENGTH_SHORT).show();

                            }
                        } catch (Exception e) {
                            dialogSubmit.dismiss();
                            Log.e("Error", "" + e);
                        }
                    } catch (Exception e) {
                        dialogSubmit.dismiss();
                        Log.e("Error", "" + e);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogSubmit.dismiss();
                Log.i("Response Error:", "" + t);
            }
        });
    }

    private void sendtodb1(int id, String details) {

        try {
            JSONObject jsonObject = new JSONObject(details);
            Call<ResponseBody> call = apiInterface.addsubcription(new Subscription(jsonObject.getJSONObject("response").getString("id"), jsonObject.getJSONObject("response").getString("state"), id, amount, payment_type));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                    if (responseData.isSuccessful()) {
                        try {
                            String stringResponse = responseData.body().string();
                            try {
                                JSONObject jsonResponseObject = new JSONObject(stringResponse);
                                if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                    dialogSubmit.dismiss();
                                    Toast.makeText(SubscriptionActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.setClass(SubscriptionActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(SubscriptionActivity.this, "Please check your internet Connection", Toast.LENGTH_LONG).show();
                                    dialogSubmit.dismiss();
                                }
                            } catch (Exception e) {
                                dialogSubmit.dismiss();
                                Log.e("Error", "" + e);
                            }
                        } catch (Exception e) {
                            dialogSubmit.dismiss();
                            Log.e("Error", "" + e);
                        }
                    } else {
                        Toast.makeText(SubscriptionActivity.this, "Please check your internet Connection" + responseData, Toast.LENGTH_LONG).show();
                        dialogSubmit.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialogSubmit.dismiss();
                    Log.i("Response Error:", "" + t);
                }
            });
        } catch (JSONException e) {
            dialogSubmit.dismiss();
            Log.i("Response Error:", "" + e);
        }

    }

    public void btnMonthly(View v){

        amount = monthly;
        payment_type = 0;
        monthlyLayout.setBackground(getResources().getDrawable(R.color.colorAccent));
        annualLayout.setBackground(getResources().getDrawable(R.drawable.subs_box));
    }

    public void btnAnnual(View v){
        amount = annual;
        payment_type = 1;
        monthlyLayout.setBackground(getResources().getDrawable(R.drawable.subs_box));
        annualLayout.setBackground(getResources().getDrawable(R.color.colorAccent));

    }

    public void btnBack(View v){

        finish();

    }
}