package com.uc.vlogshippedclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.adapter.ChatHistoryAdapter;
import com.uc.vlogshippedclient.adapter.MessageAdapter;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.ChatHistory;
import com.uc.vlogshippedclient.model.Content;
import com.uc.vlogshippedclient.util.APIClient;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private APIInterface apiInterface;
    private ProgressDialog dialog;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<ChatHistory> chatHistoryList = new ArrayList<>();
    private ChatHistoryAdapter chatHistoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        setupviews();
    }

    private void setupviews() {

        rvHistory = findViewById(R.id.rv_history);


        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface = SponsorActivity.getApiInterface();

        dialog = ProgressDialog.show(ChatHistoryActivity.this, "",
                "Getting Chat Details, Please wait...", true);


        chatHistoryAdapter = new ChatHistoryAdapter(ChatHistoryActivity.this,chatHistoryList, apiInterface, dialog);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ChatHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.setAdapter(chatHistoryAdapter);

        Call<ResponseBody> call = apiInterface.getchat(UserDb.getUserAccount().getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                                JSONArray bookList = jsonResponseObject.getJSONArray(SponsorActivity.Params.RESPONSE);
                                if(bookList.length()>0){
                                    for (int i=0;i<bookList.length();i++){
                                        JSONObject bookLists = bookList.getJSONObject(i);
                                        if(bookLists.length()>0){
                                            ChatHistory chatHistory = new ChatHistory();
                                            chatHistory.setChat_to(bookLists.getString("chat_to"));
                                            chatHistory.setChat_from(bookLists.getString("chat_from"));
                                            chatHistory.setChat_id(bookLists.getString("chat_id"));
                                            chatHistory.setChat_message(bookLists.getString("chat_message"));

                                            chatHistoryList.add(chatHistory);

                                        }
                                    }
                                    Log.i("String", ""+bookList);
                                    chatHistoryAdapter.notifyDataSetChanged();
                                    //dialog.dismiss();

                                }else{
                                    //dialogContent.dismiss();
                                    dialog.dismiss();
                                }
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
                Toast.makeText(ChatHistoryActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
