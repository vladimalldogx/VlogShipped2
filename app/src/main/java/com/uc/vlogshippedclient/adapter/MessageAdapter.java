package com.uc.vlogshippedclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.model.Messages;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private Context context;
    private List<Messages> messages;

    public MessageAdapter(Context context, List<Messages> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_layout, parent, false);

        return new MessageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {


        if(messages.get(position).getChatFrom().compareTo(String.valueOf(UserDb.getUserAccount().getId()))==0){
            holder.yourMessage.setText(messages.get(position).getChatMessage());
            holder.receiveMessage.setVisibility(View.GONE);
        }else{
            holder.receiveMessage.setText(messages.get(position).getChatMessage());
            holder.yourMessage.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        private TextView yourMessage;
        private TextView receiveMessage;

        public MessageHolder(View view) {
            super(view);


            yourMessage  = view.findViewById(R.id.your_message);
            receiveMessage = view.findViewById(R.id.receive_message);

        }
    }
}
