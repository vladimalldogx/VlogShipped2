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
import com.uc.vlogshippedclient.model.Sampling;
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

public class AddSamplingActivity extends AppCompatActivity implements com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {
    
    private Button btnSubmit;
    private EditText editTitle, editEndDate, editStartDate, editAbout, editRequirements;
    private ImageView imageProduct;
    private Spinner spinnerStartTime, spinnerEndTime, spinnerCategory;

    private StorageReference mStorageRef;
    private Calendar myCalendar;

    private Uri contentPicture;
    private APIInterface apiInterface;

    private String photoUrl;
    private ProgressDialog dialog;

    private SimpleDateFormat simpleDateFormat;
    private int year, month, day,  dayofthemonth, date;
    public static final int RESULT_GALLERY = 0;

    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> category = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sampling);
        
        setupviews();
    }

    private void setupviews() {

        editTitle = findViewById(R.id.edit_title);
        editEndDate = findViewById(R.id.edit_enddate);
        editAbout = findViewById(R.id.edit_about);
        editRequirements = findViewById(R.id.edit_requirements);
        imageProduct = findViewById(R.id.product_img);
        editStartDate = findViewById(R.id.edit_startdate);

        btnSubmit = findViewById(R.id.btn_submit);

        spinnerStartTime = findViewById(R.id.spinner_starttime);
        spinnerEndTime = findViewById(R.id.spinner_endtime);
        spinnerCategory = findViewById(R.id.spinner_category);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        FirebaseApp.initializeApp(this);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        mStorageRef = storage.getReferenceFromUrl("gs://vlogshippedthesis.appspot.com");

        myCalendar = Calendar.getInstance();

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        day = calendar.get(Calendar.DAY_OF_WEEK)-1;
        month = calendar.get(Calendar.MONTH);
        dayofthemonth = calendar.get(Calendar.DAY_OF_MONTH);

        category.add("SELECT CATEGORY");
        category.add("FASHION");
        category.add("TRAVEL");
        category.add("BEAUTY");
        category.add("OTHER");

        time.add("SELECT TIME");
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

        CustomSpinnerAdapater customSpinnerAdapter = new CustomSpinnerAdapater(AddSamplingActivity.this, time);
        spinnerStartTime.setAdapter(customSpinnerAdapter);
        spinnerEndTime.setAdapter(customSpinnerAdapter);

        CustomSpinnerAdapater customSpinnerAdapter1 = new CustomSpinnerAdapater(AddSamplingActivity.this, category);
        spinnerCategory.setAdapter(customSpinnerAdapter1);

        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(year, month, dayofthemonth, R.style.DatePickerSpinner);
                date = 0;
            }
        });

        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(year, month, dayofthemonth, R.style.DatePickerSpinner);
                date = 1;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(AddSamplingActivity.this, "",
                        "Sumitting Sampling, Please wait...", true);
                
                if(isEmpty()){
                    dialog.dismiss();
                    Toast.makeText(AddSamplingActivity.this, "Please Fill-up the missing blanks", Toast.LENGTH_SHORT).show();
                }else{
                    getUrl();
                }
                
            }
        });


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
                        Toast.makeText(AddSamplingActivity.this, ""+task.getResult(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendtodb() {
        String title = editTitle.getText().toString();
        String start_date = editStartDate.getText().toString();
        String start_time = spinnerStartTime.getSelectedItem().toString();
        String end_date =  editEndDate.getText().toString();
        String end_time = spinnerEndTime.getSelectedItem().toString();
        String about_product = editAbout.getText().toString();
        String photo_url = photoUrl;
        String requirements = editRequirements.getText().toString();

        Call<ResponseBody> call = apiInterface.addsampling(new Sampling(0, UserDb.getUserAccount().getId(),title, start_date, start_time
                , end_date, end_time, about_product, photo_url, requirements, spinnerCategory.getSelectedItem().toString()));

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
                                Toast.makeText(AddSamplingActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(AddSamplingActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }else{
                                Toast.makeText(AddSamplingActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AddSamplingActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
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

    public void btnBrowse(View v){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RESULT_GALLERY:
                if(null != data){
                    Glide.with(this).load(data.getData()).into(imageProduct);
                    contentPicture = data.getData();
                }
                break;
            default:
                break;
        }
    }

    private boolean isEmpty() {
        if(editTitle.getText().toString().isEmpty() ||
                editEndDate.getText().toString().isEmpty() ||
                editAbout.getText().toString().isEmpty() ||
                editRequirements.getText().toString().isEmpty() ||
                editStartDate.getText().toString().isEmpty() ||
                contentPicture == null ||
                spinnerStartTime.getSelectedItemPosition() == 0 ||
                spinnerEndTime.getSelectedItemPosition() == 0 ||
                spinnerCategory.getSelectedItemPosition() == 0){

            return true;

        }else{

            return false;
        }
    }
    
    public void btnBack(View v){
        Intent i = new Intent(AddSamplingActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        if(date == 0){
            editStartDate.setText(simpleDateFormat.format(calendar.getTime()));
        }else{
            editEndDate.setText(simpleDateFormat.format(calendar.getTime()));
        }
    }
}
