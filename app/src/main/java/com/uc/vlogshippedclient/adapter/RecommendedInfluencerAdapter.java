package com.uc.vlogshippedclient.adapter;

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
import com.uc.vlogshippedclient.activities.InfluencerListActivity;
import com.uc.vlogshippedclient.activities.ProfileDetailActivity;
import com.uc.vlogshippedclient.model.RecommendedInfluencer;

import java.util.List;

public class RecommendedInfluencerAdapter extends RecyclerView.Adapter<RecommendedInfluencerAdapter.RecommendedInfluencerHolder> {

    private Context context;
    private List<RecommendedInfluencer> recommendedInfluencersLists;


    public RecommendedInfluencerAdapter(Context context, List<RecommendedInfluencer> recommendedInfluencersLists) {
        this.context = context;
        this.recommendedInfluencersLists = recommendedInfluencersLists;
    }

    @NonNull
    @Override
    public RecommendedInfluencerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommended_layout, parent, false);

        return new RecommendedInfluencerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedInfluencerHolder holder, final int position) {

        if(recommendedInfluencersLists.get(position).getProfile_picture().isEmpty() || recommendedInfluencersLists.get(position).getProfile_picture().compareToIgnoreCase("none")==0){
            Glide.with(context).load(context.getDrawable(R.drawable.pp)).into(holder.profileFirst);
        }else{
            Glide.with(context).load(recommendedInfluencersLists.get(position).getProfile_picture()).into(holder.profileFirst);
        }

        holder.nameFirst.setText(recommendedInfluencersLists.get(position).getFirst_name()+" "+recommendedInfluencersLists.get(position).getLast_name());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SponsorActivity.influencerFirstName = recommendedInfluencersLists.get(position).getFirst_name();
                SponsorActivity.influencerLastName = recommendedInfluencersLists.get(position).getLast_name();
                SponsorActivity.influencerEmailAddress = recommendedInfluencersLists.get(position).getEmail_address();
                SponsorActivity.influencerID = recommendedInfluencersLists.get(position).getId();
                SponsorActivity.influencerBirthday = recommendedInfluencersLists.get(position).getBirthday();
                SponsorActivity.influencerProfile = recommendedInfluencersLists.get(position).getProfile_picture();
                SponsorActivity.influencerWebsite = recommendedInfluencersLists.get(position).getWebsite();
                SponsorActivity.influencerRateAverage = recommendedInfluencersLists.get(position).getRate_average();

                Intent intent = new Intent();
                intent.setClass(context, ProfileDetailActivity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recommendedInfluencersLists.size();
    }

    public class RecommendedInfluencerHolder extends RecyclerView.ViewHolder {

        private LinearLayout container;
        private ImageView profileFirst;
        private TextView nameFirst;


        public RecommendedInfluencerHolder(View view) {
            super(view);

            container = view.findViewById(R.id.container);
            profileFirst = view.findViewById(R.id.profile_1);
            nameFirst = view.findViewById(R.id.name_1);

        }
    }
}
