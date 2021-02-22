package com.uc.vlogshippedclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.activities.SubscriptionHistoryActivity;
import com.uc.vlogshippedclient.model.SubscriptionHistory;

import java.util.List;

public class SubscriptionHistoryAdapter extends RecyclerView.Adapter<SubscriptionHistoryAdapter.SubscriptionHistoryHolder>{

    private Context context;
    private List<SubscriptionHistory> historyList;


    public SubscriptionHistoryAdapter(Context context, List<SubscriptionHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public SubscriptionHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subscription_history_list, parent, false);
        return new SubscriptionHistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionHistoryHolder holder, int position) {


        holder.tvAmoount.setText(""+historyList.get(position).getAmount());
        holder.tvPaymentID.setText("Payment ID: "+ historyList.get(position).getPayment_id());
        holder.tvCreated.setText("Created: "+historyList.get(position).getCreated());
        holder.tvExpiry.setText("Expire: "+historyList.get(position).getExpiry_day());

        if(historyList.get(position).getPayment_type() == 0){
            holder.tvPlans.setText("Starter Plan");
            holder.tvType.setText("Monthly");
        }else{
            holder.tvPlans.setText("Pro Plan");
            holder.tvType.setText("Annual");
        }



    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class SubscriptionHistoryHolder extends RecyclerView.ViewHolder {

        private TextView tvPlans, tvAmoount, tvType, tvPaymentID, tvCreated, tvExpiry;
        private LinearLayout container;

        public SubscriptionHistoryHolder(View view) {
            super(view);

            tvPlans = view.findViewById(R.id.tv_plans);
            tvAmoount = view.findViewById(R.id.tv_amount);
            tvType = view.findViewById(R.id.tv_type);
            tvPaymentID = view.findViewById(R.id.tv_payment_id);
            tvCreated = view.findViewById(R.id.tv_created);
            tvExpiry = view.findViewById(R.id.tv_expire);
            container = view.findViewById(R.id.container);
        }
    }


}
