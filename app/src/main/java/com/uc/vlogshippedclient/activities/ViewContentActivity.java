package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Rating;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewContentActivity extends AppCompatActivity {

    private ImageView imageView, btnBackArrow;
    private TextView tvDescription;
    private RatingBar rateBar;
    private LinearLayout descriptionLayout;
    private RelativeLayout btnLayout;

    private VideoView contentVideo;

    private APIInterface apiInterface;
    private ProgressDialog dialog;

    private String contentType;
    private int visible = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_content);
        
        setupviews();
    }

    private void setupviews() {

        imageView = findViewById(R.id.imageView);
        tvDescription = findViewById(R.id.tv_description);
        descriptionLayout = findViewById(R.id.description_layout);
        btnLayout = findViewById(R.id.btn_layout);
        btnBackArrow = findViewById(R.id.btn_back_arrow);
        rateBar = findViewById(R.id.rate);
        contentVideo = findViewById(R.id.video);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        if(isVideo(SponsorActivity.contentPhotoUrl)){
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(contentVideo);
            imageView.setVisibility(View.GONE);
            contentVideo.setVisibility(View.VISIBLE);
            contentVideo.setMediaController(mediaController);
            contentVideo.setKeepScreenOn(true);
            contentVideo.setVideoPath(SponsorActivity.contentPhotoUrl);
            contentVideo.start();
//            holder.contentVideo.setVisibility(View.VISIBLE);
//            holder.contentPhoVid.setVisibility(View.GONE);
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    videoView.start();
//                }
//            });
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    videoView.stopPlayback();
//                }
//            });
        }else{
            contentVideo.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(SponsorActivity.contentPhotoUrl).into(imageView);
           // Glide.with(this).load(VlogshippedApplication.contentPhotoUrl).into(imageEdit);
        }



        //Glide.with(this).load(SponsorActivity.contentPhotoUrl).into(imageView);
        tvDescription.setText(SponsorActivity.contentDescription);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visible == 0){
                    descriptionLayout.setVisibility(View.VISIBLE);
                    btnLayout.setVisibility(View.VISIBLE);
                    btnBackArrow.setVisibility(View.GONE);
                    visible++;
                }else{
                    descriptionLayout.setVisibility(View.GONE);
                    btnLayout.setVisibility(View.GONE);
                    btnBackArrow.setVisibility(View.VISIBLE);
                    visible = 0;
                }

            }
        });
    }

    private boolean isVideo(final String path) {
        if(URLUtil.isValidUrl(path)){
            Thread thread =  new Thread(){
                @Override
                public void run(){
                    URLConnection connection = null;
                    try{
                        connection = new URL(path).openConnection();
                    }catch (IOException e){

                    }

                    Map<String, List<String>> map = connection.getHeaderFields();
                    for(Map.Entry<String, List<String>> entry : map.entrySet()){
                        Log.i("Sets", "Key : "+ entry.getKey() +
                                " , Value : "+entry.getValue());
                    }

                    contentType = connection.getHeaderField("Content-Type");
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "zzzz", Toast.LENGTH_LONG).show();
        }
        return  contentType != null && contentType.startsWith("video/");
    }

    public void btnBack(View v){
        finish();
    }

    public void btnRate(View v){

        dialog = ProgressDialog.show(ViewContentActivity.this, "",
                "Adding Rate, Please wait...", true);

        Call<ResponseBody> call = apiInterface.addrating(new Rating(UserDb.getUserAccount().getId(), SponsorActivity.influencerID, (int) rateBar.getRating(),  SponsorActivity.contentId));
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
                                Toast.makeText(ViewContentActivity.this, "Rated Successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }else{
                                Toast.makeText(ViewContentActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ViewContentActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
