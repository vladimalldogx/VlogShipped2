package com.uc.vlogshippedclient.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.model.MyCampaign;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {


    private Context context;
    private int count;
    private int layoutPosition = 0;

    public CategoryAdapter(Context context, int i, int layoutPosition) {

        this.context = context;
        this.count = i;
        this.layoutPosition = layoutPosition;

    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list, parent, false);


        return new CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, final int position) {

        //Toast.makeText(context, ""+count, Toast.LENGTH_SHORT).show();



        if(layoutPosition == position){
            holder.container.setBackgroundResource(R.drawable.category_select);
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            holder.container.setBackgroundResource(R.drawable.category_unselect);
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.black));
        }

        if(position == 0){
            holder.tvCategory.setText("ALL");
        }else if(position == 1){
            holder.tvCategory.setText("Following");
        }else if(position == 2){
            holder.tvCategory.setText("Fashion");
        }else if(position == 3){
            holder.tvCategory.setText("Travel");
        }else if(position == 4){
            holder.tvCategory.setText("Beauty");
        }else if(position == 5){
            holder.tvCategory.setText("Other");
        }




        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutPosition = position;

                String category = holder.tvCategory.getText().toString();
                Intent intent = new Intent("custom-message");
                intent.putExtra("layoutposition", ""+layoutPosition);
                intent.putExtra("category",category);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }
        });


    }



    @Override
    public int getItemCount() {
        return count;
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {

        private TextView tvCategory;
        private LinearLayout container;

        public CategoryHolder(View view) {
            super(view);


            tvCategory = view.findViewById(R.id.tv_category);
            container = view.findViewById(R.id.container);

        }
    }


}
