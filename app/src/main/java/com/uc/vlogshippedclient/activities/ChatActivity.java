package com.uc.vlogshippedclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.adapter.MessageAdapter;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Message;
import com.uc.vlogshippedclient.model.Messages;
import com.uc.vlogshippedclient.model.NotifcationModel;
import com.uc.vlogshippedclient.model.User;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText message;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Message chatMessage;
    private RecyclerView rvChat;

    private List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    private APIInterface apiInterface;

    private String notification_from;
    private String chat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();


        notification_from = extras.getString("notification_from");
        chat_id = extras.getString("chat_id");

        setupviews();
        
    }

    private void setupviews() {

        message = findViewById(R.id.edit_message);
        rvChat = findViewById(R.id.rv_chat);


        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        messageAdapter = new MessageAdapter(ChatActivity.this,messagesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        rvChat.setLayoutManager(mLayoutManager);
        rvChat.setItemAnimator(new DefaultItemAnimator());
        rvChat.setAdapter(messageAdapter);

        receivemessage();
        
    }

    private void receivemessage() {

        firebaseDatabase = FirebaseDatabase.getInstance();


        databaseReference = firebaseDatabase.getReference("Messages").child(String.valueOf(chat_id));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() || dataSnapshot.getChildrenCount()  != 0){

                    messagesList.clear();

                    for(DataSnapshot childkey : dataSnapshot.getChildren()){

                        Messages messages = new Messages();

                        messages.setChatMessage(childkey.child("chatMessage").getValue().toString());
                        messages.setChatFrom(childkey.child("chatFrom").getValue().toString());
                        messages.setChatTo(childkey.child("chatTo").getValue().toString());



                        messagesList.add(messages);
                    }

                    messageAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void btnSend(View v){

        if(chat_id.compareTo("0")== 0){
            sendtodb();
        }else{
            updatedb();

        }


    }

    private void updatedb() {
        Call<ResponseBody> call = apiInterface.updatechat(Integer.parseInt(chat_id), message.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                //JSONObject response = jsonResponseObject.getJSONObject(SponsorActivity.Params.RESPONSE);


                                firebaseDatabase = FirebaseDatabase.getInstance();
                                databaseReference = firebaseDatabase.getReference("Messages").child(String.valueOf(chat_id));
                                chatMessage = new Message(message.getText().toString(), chat_id,
                                        String.valueOf(notification_from), String.valueOf(UserDb.getUserAccount().getId()));
                                databaseReference.push().setValue(chatMessage);

                                message.setText("");

                                receivemessage();


                            }else{
                                Toast.makeText(ChatActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();

                            }
                        }catch (Exception e){
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        Log.e("Error", ""+e);
                    }
                }else{
                    Toast.makeText(ChatActivity.this, "Please check your internet Connection "+responseData, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "No Internet Connection"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendtodb() {
        Call<ResponseBody> call = apiInterface.addchat(Integer.parseInt(notification_from), UserDb.getUserAccount().getId(), message.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {

                                JSONObject response = jsonResponseObject.getJSONObject(SponsorActivity.Params.RESPONSE);

                                firebaseDatabase = FirebaseDatabase.getInstance();
                                databaseReference = firebaseDatabase.getReference("Messages").child(String.valueOf(response.getString("chat_id")));
                                chatMessage = new Message(message.getText().toString(), response.getString("chat_id"),
                                        String.valueOf(notification_from), String.valueOf(UserDb.getUserAccount().getId()));
                                databaseReference.push().setValue(chatMessage);

                                chat_id = String.valueOf(response.getString("chat_id"));

                                message.setText("");

                                receivemessage();

                                //dialogSubmit.dismiss();

                            }else{
                                Toast.makeText(ChatActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                                //dialogSubmit.dismiss();
                            }
                        }catch (Exception e){
                            //dialogSubmit.dismiss();
                            Log.e("Error", ""+e);
                        }
                    }catch (Exception e){
                        //dialogSubmit.dismiss();
                        Log.e("Error", ""+e);
                    }
                }else{
                    Toast.makeText(ChatActivity.this, "Please check your internet Connection "+responseData, Toast.LENGTH_SHORT).show();
                    //dialogSubmit.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "No Internet Connection"+t, Toast.LENGTH_SHORT).show();
                //dialogSubmit.dismiss();
            }
        });
    }

    public void btnBack(View v){
        finish();
    }
}
