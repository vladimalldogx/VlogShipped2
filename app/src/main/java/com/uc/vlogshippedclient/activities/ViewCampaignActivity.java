package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.MyCampaign;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCampaignActivity extends AppCompatActivity {

    private ImageView campaignPhoto;
    private TextView tvTitle, tvTime, tvLink, tvDescription, price_range;
    private ProgressDialog dialog;

    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_campaign);

        setupviews();
    }

    private void setupviews() {
        campaignPhoto = findViewById(R.id.img_picture);
        tvTitle = findViewById(R.id.tv_title);
        tvTime = findViewById(R.id.tv_time);
        tvLink = findViewById(R.id.tv_link);
        tvDescription = findViewById(R.id.tv_description);
        price_range = findViewById(R.id.price_range);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        Glide.with(this).load(SponsorActivity.campaignPhoto).into(campaignPhoto);
        tvTitle.setText(SponsorActivity.campaignTitle);
        tvLink.setText(SponsorActivity.campaignLink);
        tvTime.setText(SponsorActivity.campaignDate);
        tvDescription.setText(SponsorActivity.campaignDescription);
        price_range.setText(SponsorActivity.campaignPriceRange);
    }

    public void btnEdit(View v){
        Intent i = new Intent(ViewCampaignActivity.this, EditCampaignActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void btnDelete(View v){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewCampaignActivity.this);

        alertDialogBuilder.setMessage("Are you sure you want to delete this campaign?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog2, int which) {
                dialog = ProgressDialog.show(ViewCampaignActivity.this, "",
                        "Deleting Campaign, Please wait...", true);
                delete();
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

    private void delete() {
        Call<ResponseBody> call = apiInterface.deletecampaign(new MyCampaign(UserDb.getUserAccount().getId(), SponsorActivity.campaignID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                dialog.dismiss();
                                Toast.makeText(ViewCampaignActivity.this, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(ViewCampaignActivity.this, CampaignActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }else{
                                Toast.makeText(ViewCampaignActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ViewCampaignActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Log.i("Response Error:", ""+t);
            }
        });
    }

    public void btnBack(View v){
        Intent i = new Intent(ViewCampaignActivity.this, CampaignActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
