package com.uc.vlogshippedclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.activities.CampaignActivity;
import com.uc.vlogshippedclient.activities.ViewCampaignActivity;
import com.uc.vlogshippedclient.model.MyCampaign;

import java.util.ArrayList;
import java.util.List;

public class CampaignAdapter  extends RecyclerView.Adapter<CampaignAdapter.CampaignHolder>  {


    private Context context;
    private List<MyCampaign> campaigns;

    public CampaignAdapter(Context context, List<MyCampaign> campaigns) {
        this.campaigns = campaigns;
        this.context = context;
    }

    public class CampaignHolder extends RecyclerView.ViewHolder {

        private ImageView campaignPhoto;
        private TextView tvtitle;
        private TextView tvDate;
        private LinearLayout container;

        public CampaignHolder(View view) {
            super(view);

            campaignPhoto = view.findViewById(R.id.img_picture);
            tvtitle = view.findViewById(R.id.tv_title);
            tvDate = view.findViewById(R.id.tv_date);
            container = view.findViewById(R.id.container);
        }
    }

    @NonNull
    @Override
    public CampaignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.campaign_layout, parent, false);

        return new CampaignHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignHolder holder, final int position) {

        Glide.with(context).load(campaigns.get(position).getPhoto_url()).into(holder.campaignPhoto);
        holder.tvDate.setText(campaigns.get(position).getStart_date()+" "+campaigns.get(position).getStart_time()+" - "+campaigns.get(position).getEnd_date()+" "+campaigns.get(position).getEnd_time());
        holder.tvtitle.setText(campaigns.get(position).getTitle());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SponsorActivity.campaignID = campaigns.get(position).getId();
                SponsorActivity.campaignTitle = campaigns.get(position).getTitle();
                SponsorActivity.campaignDate = campaigns.get(position).getStart_date()+" "+campaigns.get(position).getStart_time()+" - "+campaigns.get(position).getEnd_date()+" "+campaigns.get(position).getEnd_time();
                SponsorActivity.campaignLink = campaigns.get(position).getProduct_url();
                SponsorActivity.campaignPhoto = campaigns.get(position).getPhoto_url();
                SponsorActivity.campaignDescription = campaigns.get(position).getDescription();
                SponsorActivity.campaignStartDate = campaigns.get(position).getStart_date();
                SponsorActivity.campaignEndDate = campaigns.get(position).getEnd_date();
                SponsorActivity.campaignStartTime = campaigns.get(position).getStart_time();
                SponsorActivity.campaignEndTime = campaigns.get(position).getEnd_time();
                SponsorActivity.campaignCategory = campaigns.get(position).getCategory();
                SponsorActivity.campaignPriceRange = campaigns.get(position).getPrice_range();



                Intent intent = new Intent();
                intent.setClass(context, ViewCampaignActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();

            }
        });


    }

    @Override
    public int getItemCount() {
        return campaigns.size();
    }


}
