package com.uc.vlogshippedclient.adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.activities.InfluencerListActivity;
import com.uc.vlogshippedclient.activities.ProfileDetailActivity;
import com.uc.vlogshippedclient.model.Influencer;

import java.util.List;

public class InfluencerListAdapter extends RecyclerView.Adapter<InfluencerListAdapter.InfluencerHolder>  {

    private Context context;
    private List<Influencer> influencers;

    public InfluencerListAdapter(Context context, List<Influencer> influencers) {
        this.context = context;
        this.influencers = influencers;

    }

    public class InfluencerHolder extends RecyclerView.ViewHolder {

        private LinearLayout container, container1;
        private ImageView profileFirst, profileSecond;
        private TextView nameFirst, nameSecond;


        public InfluencerHolder(View view) {
            super(view);

            container = view.findViewById(R.id.container);
            container1 = view.findViewById(R.id.container_1);
            profileFirst = view.findViewById(R.id.profile_1);
            profileSecond = view.findViewById(R.id.profile_2);
            nameFirst = view.findViewById(R.id.name_1);
            nameSecond = view.findViewById(R.id.name_2);

        }
    }

    @NonNull
    @Override
    public InfluencerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.influencer_list, parent, false);

        return new InfluencerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InfluencerHolder holder, int position) {

        final int pos1 = position;


        if(position > influencers.size() && position != influencers.size()){


        }else {
            position *=2;

            if(influencers.get(position).getProfile_picture().isEmpty() || influencers.get(position).getProfile_picture().compareToIgnoreCase("none")==0){
                Glide.with(context).load(context.getDrawable(R.drawable.pp)).into(holder.profileFirst);
            }else{
                Glide.with(context).load(influencers.get(position).getProfile_picture()).into(holder.profileFirst);
            }

            holder.nameFirst.setText(influencers.get(position).getFirst_name()+" "+influencers.get(position).getLast_name());


            final int finalPosition = position;

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SponsorActivity.influencerFirstName = influencers.get(finalPosition).getFirst_name();
                    SponsorActivity.influencerLastName = influencers.get(finalPosition).getLast_name();
                    SponsorActivity.influencerEmailAddress = influencers.get(finalPosition).getEmail_address();
                    SponsorActivity.influencerID = influencers.get(finalPosition).getId();
                    SponsorActivity.influencerBirthday = influencers.get(finalPosition).getBirthday();
                    SponsorActivity.influencerProfile = influencers.get(finalPosition).getProfile_picture();
                    SponsorActivity.influencerRateAverage = influencers.get(finalPosition).getRate_average();
                    SponsorActivity.influencerWebsite = influencers.get(finalPosition).getWebsite();

                    Intent intent = new Intent();
                    intent.setClass(context, ProfileDetailActivity.class);
                    context.startActivity(intent);
                }
            });



            if(position + 1 != influencers.size()) {


                if(influencers.get(position + 1).getProfile_picture().isEmpty() || influencers.get(position + 1).getProfile_picture().compareToIgnoreCase("none")==0){
                    Glide.with(context).load(context.getDrawable(R.drawable.pp)).into(holder.profileSecond);
                }else{
                    Glide.with(context).load(influencers.get(position + 1).getProfile_picture()).into(holder.profileSecond);
                }

                holder.nameSecond.setText(influencers.get(position + 1).getFirst_name()+" "+influencers.get(position + 1).getLast_name());

                final int finalPosition_1 = position + 1;


                holder.container1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        SponsorActivity.influencerFirstName = influencers.get(finalPosition_1).getFirst_name();
                        SponsorActivity.influencerLastName = influencers.get(finalPosition_1).getLast_name();
                        SponsorActivity.influencerEmailAddress = influencers.get(finalPosition_1).getEmail_address();
                        SponsorActivity.influencerID = influencers.get(finalPosition_1).getId();
                        SponsorActivity.influencerBirthday = influencers.get(finalPosition_1).getBirthday();
                        SponsorActivity.influencerProfile = influencers.get(finalPosition_1).getProfile_picture();
                        SponsorActivity.influencerRateAverage = influencers.get(finalPosition_1).getRate_average();
                        SponsorActivity.influencerWebsite = influencers.get(finalPosition_1).getWebsite();

                        Intent intent = new Intent();
                        intent.setClass(context, ProfileDetailActivity.class);
                        context.startActivity(intent);
                    }
                });



            }
            else{
                holder.container1.setVisibility(View.GONE);

            }
        }





        final int pos2 = position + 1;

    }

    @Override
    public int getItemCount() {

        if(influencers.size() % 2 == 0){
            return influencers.size() / 2 ;
        }else{
            return influencers.size() / 2  + 1;
        }
        //return influencers.size();
    }


}
