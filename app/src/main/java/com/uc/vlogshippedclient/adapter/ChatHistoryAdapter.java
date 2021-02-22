package com.uc.vlogshippedclient.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.activities.ChatActivity;
import com.uc.vlogshippedclient.activities.ChatHistoryActivity;
import com.uc.vlogshippedclient.activities.NotificationInviteActivity;
import com.uc.vlogshippedclient.activities.SubscriptionHistoryActivity;
import com.uc.vlogshippedclient.activities.ViewSubscriptionActivity;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.ChatHistory;
import com.uc.vlogshippedclient.model.SubscriptionHistory;
import com.uc.vlogshippedclient.util.APIInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryHolder>  {

    private Context context;
    private List<ChatHistory> chatHistoryList;
    private APIInterface apiInterface;
    private ProgressDialog dialog;

    public ChatHistoryAdapter(Context context, List<ChatHistory> chatHistoryList, APIInterface apiInterface, ProgressDialog dialog) {

        this.context = context;
        this.chatHistoryList = chatHistoryList;
        this.apiInterface = apiInterface;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ChatHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_history_layout, parent, false);

        return new ChatHistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryHolder holder, final int position) {


        if(chatHistoryList.get(position).getChat_from().compareTo(String.valueOf(UserDb.getUserAccount().getId()))==0){
            getprofile(holder, chatHistoryList.get(position).getChat_to());
        }else{
            getprofile(holder, chatHistoryList.get(position).getChat_from());
        }

        holder.message.setText(chatHistoryList.get(position).getChat_message());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ChatActivity.class);
                intent.putExtra("chat_id", chatHistoryList.get(position).getChat_id());
                intent.putExtra("notification_from", chatHistoryList.get(position).getChat_to());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    private void getprofile(final ChatHistoryHolder holder, String id) {
        Call<ResponseBody> call = apiInterface.getchatprofile(Integer.parseInt(id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseData) {
                if(responseData.isSuccessful()){
                    try {
                        String stringResponse = responseData.body().string();
                        try {
                            JSONObject jsonResponseObject = new JSONObject(stringResponse);
                            if (jsonResponseObject.getInt(SponsorActivity.Params.STATUS) == 200) {
                             holder.name.setText(jsonResponseObject.getJSONObject("response").getString("first_name") + " " + jsonResponseObject.getJSONObject("response").getString("last_name"));
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
                Toast.makeText(context, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatHistoryList.size();
    }

    public class ChatHistoryHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView message;
        private LinearLayout container;

        public ChatHistoryHolder(View view) {
            super(view);


            name  = view.findViewById(R.id.name);
            message = view.findViewById(R.id.receive_message);
            container = view.findViewById(R.id.container);

        }
    }
}
