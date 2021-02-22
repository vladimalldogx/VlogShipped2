package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.adapter.CustomSpinnerAdapater;
import com.uc.vlogshippedclient.model.Register;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Spinner spinner_category;
    private EditText email, password, cPassword, fName, lName, bDay, cNumber, companyName, website;
    private RadioButton gMale, gFemale;
    private Button btnRegister;

    private Register registerModel;

    private ArrayList<String> category = new ArrayList<>();

    private APIInterface apiInterface;

    private ProgressDialog dialogRegistering;

    private String gender = "male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        setupviews();
    }

    private void setupviews() {

        spinner_category = findViewById(R.id.spinner_category);
        email = findViewById(R.id.email_address);
        password = findViewById(R.id.password);
        cPassword = findViewById(R.id.c_password);
        fName = findViewById(R.id.f_name);
        lName = findViewById(R.id.l_name);
        bDay = findViewById(R.id.birthdate);
        cNumber = findViewById(R.id.contact_number);
        gMale = findViewById(R.id.g_male);
        gFemale = findViewById(R.id.g_female);
        btnRegister = findViewById(R.id.btn_sign_up);
        companyName = findViewById(R.id.company_name);
        website = findViewById(R.id.website);


        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        debug();

        category.add("SELECT CATEGORY");
        category.add("FASHION");
        category.add("TRAVEL");
        category.add("BEAUTY");
        category.add("OTHER");

        gMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gFemale.setChecked(false);
                gender = "male";
                Toast.makeText(RegisterActivity.this, ""+gender, Toast.LENGTH_SHORT).show();
            }
        });

        gFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gMale.setChecked(false);
                gender = "female";
                Toast.makeText(RegisterActivity.this, ""+gender, Toast.LENGTH_SHORT).show();

            }
        });

        CustomSpinnerAdapater customSpinnerAdapter = new CustomSpinnerAdapater(RegisterActivity.this, category);
        spinner_category.setAdapter(customSpinnerAdapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogRegistering = ProgressDialog.show(RegisterActivity.this, "",
                        "Registering, Please wait...", true);
                if(isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill up the missing fields.", Toast.LENGTH_SHORT).show();
                    dialogRegistering.dismiss();
                }else {

                    if(password.getText().toString().compareTo(cPassword.getText().toString()) == 0){

                        dialogRegistering.dismiss();
                        proceed();

                    }else{
                        Toast.makeText(RegisterActivity.this, "Password not matched", Toast.LENGTH_SHORT).show();
                        dialogRegistering.dismiss();
                    }


                }
            }
        });

    }

    private void proceed() {

        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this, SubscriptionActivity.class);
        intent.putExtra("first_name", fName.getText().toString());
        intent.putExtra("last_name", lName.getText().toString());
        intent.putExtra("contact", cNumber.getText().toString());
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("birthday", bDay.getText().toString());
        intent.putExtra("password", password.getText().toString());
        intent.putExtra("gender", gender);
        intent.putExtra("category", spinner_category.getSelectedItem().toString());
        intent.putExtra("website", website.getText().toString());
        intent.putExtra("company", companyName.getText().toString());
        startActivity(intent);

    }

    private boolean isEmpty() {
        if(fName.getText().toString().isEmpty() ||
                lName.getText().toString().isEmpty() ||
                cNumber.getText().toString().isEmpty() ||
                bDay.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                cPassword.getText().toString().isEmpty() ||
                companyName.getText().toString().isEmpty() ||
                website.getText().toString().isEmpty() ||
                email.getText().toString().isEmpty() ||
                spinner_category.getSelectedItemPosition() == 0){

            return true;

        }else{

            return false;
        }
    }

    private void debug() {
        email.setText("sample1@gmail.com");
        password.setText("sample");
        cPassword.setText("sample");
        fName.setText("sample");
        lName.setText("sample");
        cNumber.setText("09994093374");
        bDay.setText("11-11-2012");
        companyName.setText("ALX COMPANY");
        website.setText("www.fb.com");
        gMale.setChecked(true);
    }
}
