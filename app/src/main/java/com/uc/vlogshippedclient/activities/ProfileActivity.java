package com.uc.vlogshippedclient.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.EditUser;
import com.uc.vlogshippedclient.model.User;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText companyName, firstName, lastName, website, contactNumber, description;
    private Button btnUpdate;
    private ProgressDialog dialogEditing;
    private APIInterface apiInterface;
    private CircleImageView profilePicture;

    private StorageReference mStorageRef;
    private String photoUrl;

    public static final int RESULT_GALLERY = 0;
    private Uri contentPicture;
    private int click = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        FirebaseApp.initializeApp(this);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        mStorageRef = storage.getReferenceFromUrl("gs://vlogshippedthesis.appspot.com");

       // Toast.makeText(this, ""+mStorageRef.getStorage(), Toast.LENGTH_SHORT).show();

        setupviews();
    }

    private void setupviews() {

        companyName = findViewById(R.id.company_name);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        website = findViewById(R.id.website);
        contactNumber = findViewById(R.id.mobile_number);
        description = findViewById(R.id.description);
        profilePicture = findViewById(R.id.imageView);

        btnUpdate = findViewById(R.id.btn_update);

        companyName.setEnabled(false);
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        website.setEnabled(false);
        contactNumber.setEnabled(false);
        description.setEnabled(false);
        profilePicture.setEnabled(false);

        companyName.setText(UserDb.getUserAccount().getCompanyName());
        firstName.setText(UserDb.getUserAccount().getFirst_name());
        lastName.setText(UserDb.getUserAccount().getLast_name());
        website.setText(UserDb.getUserAccount().getWebsite());
        contactNumber.setText(UserDb.getUserAccount().getMobile_number());
        description.setText(UserDb.getUserAccount().getDescription());

        //Toast.makeText(this, ""+ UserDb.getUserAccount().getProfile_picture(), Toast.LENGTH_SHORT).show();

//        if(UserDb.getUserAccount().getProfile_picture().isEmpty() || UserDb.getUserAccount().getProfile_picture().compareToIgnoreCase("none") == 0){
//            Glide.with(this).load(getResources().getDrawable(R.drawable.pp)).into(profilePicture);
//        }else{
//            Glide.with(this).load(UserDb.getUserAccount().getProfile_picture()).into(profilePicture);
//        }

        photoUrl = UserDb.getUserAccount().getProfile_picture();

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                click = 1;

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_GALLERY);

            }
        });

        //Toast.makeText(this, ""+UserDb.getUserAccount().getId(), Toast.LENGTH_SHORT).show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyName.isEnabled()){

                    dialogEditing = ProgressDialog.show(ProfileActivity.this, "",
                            "Updating Profile, Please wait...", true);


                    if(click >= 1){
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
                                        Toast.makeText(ProfileActivity.this, ""+task.getResult(), Toast.LENGTH_SHORT).show();
                                        dialogEditing.dismiss();
                                    }
                                }
                            });
                        }catch (Exception e){
                            Toast.makeText(ProfileActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //photoUrl = "none";
                        sendtodb();
                    }

                }else{

                    companyName.setEnabled(true);
                    firstName.setEnabled(true);
                    lastName.setEnabled(true);
                    website.setEnabled(true);
                    contactNumber.setEnabled(true);
                    description.setEnabled(true);
                    profilePicture.setEnabled(true);

                }
            }
        });

    }

    private void sendtodb() {
        EditUser editUser = new EditUser(UserDb.getUserAccount().getId(),firstName.getText().toString(), lastName.getText().toString(),
                companyName.getText().toString(), description.getText().toString(), contactNumber.getText().toString(),
                website.getText().toString(), photoUrl);

        Call<ResponseBody> call = apiInterface.editinfo(editUser);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                dialogEditing.dismiss();

                                Toast.makeText(ProfileActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                                JSONObject response = jsonResponseObject.getJSONObject(SponsorActivity.Params.RESPONSE);
                                UserDb.updateInfo(response);

                                companyName.setEnabled(false);
                                firstName.setEnabled(false);
                                lastName.setEnabled(false);
                                website.setEnabled(false);
                                contactNumber.setEnabled(false);
                                description.setEnabled(false);
                                profilePicture.setEnabled(false);

                            }else if(jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 401){

                                dialogEditing.dismiss();
                                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            } else if(jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 406){

                                dialogEditing.dismiss();
                                Toast.makeText(ProfileActivity.this, "Email Already Taken.", Toast.LENGTH_SHORT).show();

                            }
                        }catch (Exception e){
                            dialogEditing.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        dialogEditing.dismiss();
                        Log.e("Error", ""+e);
                    }
                }else{
                    Toast.makeText(ProfileActivity.this, ""+responseData, Toast.LENGTH_SHORT).show();
                    dialogEditing.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogEditing.dismiss();
                Log.i("Response Error:", ""+t);
            }
        });
    }

    public void btnBack(View v){
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_GALLERY:
                if (null != data) {
                    Glide.with(ProfileActivity.this).load(data.getData()).into(profilePicture);
                    contentPicture = data.getData();
                   // Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "dah", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
