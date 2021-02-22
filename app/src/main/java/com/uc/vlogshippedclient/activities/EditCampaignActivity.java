package com.uc.vlogshippedclient.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.adapter.CustomSpinnerAdapater;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Campaign;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCampaignActivity extends AppCompatActivity implements com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener{

    private EditText editStartDate, editEndate, editTitle, editDescription, editProductUrl, editPriceRange;
    private Spinner spinnerStartTime, spinnerEndTime, spinnerCategory;
    private ImageView imageView;
    private Button btnEdit;

    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> endtime = new ArrayList<>();
    private ArrayList<String> category = new ArrayList<>();

    private ProgressDialog dialog;
    public static final int RESULT_GALLERY = 0;

    private StorageReference mStorageRef;

    private Uri contentPicture;
    private APIInterface apiInterface;

    private Calendar myCalendar;
    private SimpleDateFormat simpleDateFormat;
    private int year, month, day,  dayofthemonth, date;


    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_campaign);

        setupviews();
    }

    private void setupviews() {
        editStartDate = findViewById(R.id.edit_startdate);
        editEndate = findViewById(R.id.edit_enddate);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.description);
        editProductUrl = findViewById(R.id.edit_producturl);
        editPriceRange = findViewById(R.id.edit_price_range);

        spinnerStartTime = findViewById(R.id.spinner_starttime);
        spinnerEndTime = findViewById(R.id.spinner_endtime);
        spinnerCategory = findViewById(R.id.spinner_category);

        imageView = findViewById(R.id.imageView);
        btnEdit = findViewById(R.id.btn_submit);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        FirebaseApp.initializeApp(this);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        mStorageRef = storage.getReferenceFromUrl("gs://vlogshippedthesis.appspot.com");

        editStartDate.setText(SponsorActivity.campaignStartDate);
        editEndate.setText(SponsorActivity.campaignEndDate);
        editTitle.setText(SponsorActivity.campaignTitle);
        editDescription.setText(SponsorActivity.campaignDescription);
        editProductUrl.setText(SponsorActivity.campaignLink);
        editPriceRange.setText(SponsorActivity.campaignPriceRange);

        Glide.with(this).load(SponsorActivity.campaignPhoto).into(imageView);

        category.add(SponsorActivity.campaignCategory);
        category.add("FASHION");
        category.add("TRAVEL");
        category.add("BEAUTY");
        category.add("OTHER");

        time.add(SponsorActivity.campaignStartTime);
        time.add("8:00 am");
        time.add("9:00 am");
        time.add("10:00 am");
        time.add("11:00 am");
        time.add("12:00 pm");
        time.add("01:00 pm");
        time.add("02:00 pm");
        time.add("03:00 pm");
        time.add("04:00 pm");
        time.add("05:00 pm");
        time.add("06:00 pm");
        time.add("07:00 pm");
        time.add("08:00 pm");

        endtime.add(SponsorActivity.campaignEndTime);
        endtime.add("8:00 am");
        endtime.add("9:00 am");
        endtime.add("10:00 am");
        endtime.add("11:00 am");
        endtime.add("12:00 pm");
        endtime.add("01:00 pm");
        endtime.add("02:00 pm");
        endtime.add("03:00 pm");
        endtime.add("04:00 pm");
        endtime.add("05:00 pm");
        endtime.add("06:00 pm");
        endtime.add("07:00 pm");
        endtime.add("08:00 pm");

        CustomSpinnerAdapater customSpinnerAdapter = new CustomSpinnerAdapater(EditCampaignActivity.this, time);
        spinnerStartTime.setAdapter(customSpinnerAdapter);
        spinnerEndTime.setAdapter(customSpinnerAdapter);

        CustomSpinnerAdapater customSpinnerAdapter1 = new CustomSpinnerAdapater(EditCampaignActivity.this, category);
        spinnerCategory.setAdapter(customSpinnerAdapter1);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        day = calendar.get(Calendar.DAY_OF_WEEK)-1;
        month = calendar.get(Calendar.MONTH);
        dayofthemonth = calendar.get(Calendar.DAY_OF_MONTH);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(EditCampaignActivity.this, "",
                        "Updating Campaign, Please wait...", true);
                if(SponsorActivity.campaignPhoto.compareToIgnoreCase("")==0){
                    getUrl();
                }else if(isEmpty()){
                    dialog.dismiss();
                    Toast.makeText(EditCampaignActivity.this, "Please Fill-up the missing fields", Toast.LENGTH_SHORT).show();
                } else{
                  sendtodb();
                }
            }
        });

        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(year, month, dayofthemonth, R.style.DatePickerSpinner);
                date = 0;
            }
        });

        editEndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(year, month, dayofthemonth, R.style.DatePickerSpinner);
                date = 1;
            }
        });

    }

    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();

    }

    private void getUrl() {
        try {
            final StorageReference profile = mStorageRef.child("images/"+contentPicture.getLastPathSegment());
            UploadTask uploadTask = profile.putFile(contentPicture);
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Log.e("taskError", ""+task.getException());
                        throw task.getException();

                    }

                    // Continue with the task to get the download URL
                    return profile.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        photoUrl = "" + task.getResult();
                        Log.i("New Url", ""+photoUrl);
                        sendtodb();
                    } else {
                        Toast.makeText(EditCampaignActivity.this, ""+task.getResult(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendtodb() {
        String photo_url;
        int id = SponsorActivity.campaignID;
        String title = editTitle.getText().toString();
        String start_date = editStartDate.getText().toString();
        String start_time = spinnerStartTime.getSelectedItem().toString();
        String end_date =  editEndate.getText().toString();
        String end_time = spinnerEndTime.getSelectedItem().toString();
        String product_url = editProductUrl.getText().toString();
        if(SponsorActivity.campaignPhoto.compareToIgnoreCase("")==0){
            photo_url = photoUrl;
        }else{
            photo_url = SponsorActivity.campaignPhoto;
        }
        String description = editDescription.getText().toString();

        Call<ResponseBody> call = apiInterface.updatecampaign(new Campaign(id, UserDb.getUserAccount().getId(),title, start_date, start_time
                , end_date, end_time, product_url, photo_url, description, spinnerCategory.getSelectedItem().toString(), editPriceRange.getText().toString()));

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
                                Toast.makeText(EditCampaignActivity.this, "Successfully Updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(EditCampaignActivity.this, CampaignActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }else{
                                Toast.makeText(EditCampaignActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditCampaignActivity.this, "Please check your internet Connection"+responseData, Toast.LENGTH_SHORT).show();
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

    private boolean isEmpty() {
        if(editTitle.getText().toString().isEmpty() ||
                editEndate.getText().toString().isEmpty() ||
                editProductUrl.getText().toString().isEmpty() ||
                editDescription.getText().toString().isEmpty() ||
                editStartDate.getText().toString().isEmpty()){

            return true;

        }else{

            return false;
        }
    }

    public void btnBack(View v){
        Intent i = new Intent(EditCampaignActivity.this, ViewCampaignActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case AddCampaignActivity.RESULT_GALLERY:
                if(null != data){
                    Glide.with(this).load(data.getData()).into(imageView);
                    contentPicture = data.getData();
                }
                break;
            default:
                break;
        }
    }

    public void btnBrowse(View v){
        SponsorActivity.campaignPhoto = "";
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_GALLERY);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        if(date == 0){
            editStartDate.setText(simpleDateFormat.format(calendar.getTime()));
        }else{
            editEndate.setText(simpleDateFormat.format(calendar.getTime()));
        }
    }
}
