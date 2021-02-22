package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.User;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailAddress, password;
    private Button btnSignIn;
    private ProgressDialog dialogLoggingIn;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();
        setupviews();

    }

    private void setupviews() {
        btnSignIn = findViewById(R.id.btn_sign_in);
        emailAddress = findViewById(R.id.email_address);
        password = findViewById(R.id.password);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLoggingIn = ProgressDialog.show(LoginActivity.this, "",
                        "Logging in, Please wait...", true);

                loginprocess();

            }
        });
    }

    private void loginprocess() {
        if(isEmpty()){

        }else{

            User user = new User(emailAddress.getText().toString(), password.getText().toString(), 0);

            Call<ResponseBody> call = apiInterface.login(user);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                    if(responseData.isSuccessful()){
                        try {
                            String stringResponse = responseData.body().string();
                            try {
                                JSONObject jsonResponseObject = new JSONObject(stringResponse);
                                if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                    dialogLoggingIn.dismiss();
                                    JSONObject response = jsonResponseObject.getJSONObject(SponsorActivity.Params.RESPONSE);
                                    JSONObject responseObject = response.getJSONObject("user");
                                    UserDb.clearDb();
                                    UserDb.setUser(responseObject);
                                    Toast.makeText(LoginActivity.this, "Successfully Logged In.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }else if(jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 501){
                                    Toast.makeText(LoginActivity.this, "Please wait for admin's approval", Toast.LENGTH_SHORT).show();
                                    dialogLoggingIn.dismiss();
                                } else{
                                    dialogLoggingIn.dismiss();
                                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                Log.e("Error", ""+e);
                            }
                        }catch (Exception e){
                            Log.e("Error", ""+e);
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, ""+responseData, Toast.LENGTH_LONG).show();
                        dialogLoggingIn.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, ""+t, Toast.LENGTH_SHORT).show();
                    dialogLoggingIn.dismiss();
                }
            });

        }
    }

    private boolean isEmpty() {
        if(TextUtils.isEmpty(emailAddress.getText().toString())){
            emailAddress.setError("Please input your ID number");
            dialogLoggingIn.dismiss();
            return true;
        }else if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Please input your password");
            dialogLoggingIn.dismiss();
            return true;
        }
        return false;
    }

    public void register(View v){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
