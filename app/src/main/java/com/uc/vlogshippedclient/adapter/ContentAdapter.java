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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.activities.ProfileDetailActivity;
import com.uc.vlogshippedclient.activities.ViewContentActivity;
import com.uc.vlogshippedclient.model.Content;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentHolder> {

    private Context context;
    private List<Content> content;

    public ContentAdapter(Context context, List<Content> content) {
        this.context = context;
        this.content = content;
    }
    public class ContentHolder extends RecyclerView.ViewHolder {

        private ImageView contentPhoVid;
        private TextView tvCreated, tvTitle;

        public ContentHolder(View view) {
            super(view);

            contentPhoVid = view.findViewById(R.id.img_picture);
            tvCreated = view.findViewById(R.id.tv_date);
            tvTitle = view.findViewById(R.id.tv_title);

        }
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_layout, parent, false);

        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentHolder holder, final int position) {

        //final int pos1 = position;

        if(content.get(position).getThumbnail().compareTo("none") == 0){
            Glide.with(context).load(content.get(position).getUrl()).into(holder.contentPhoVid);
        }else{
            Glide.with(context).load(content.get(position).getThumbnail()).into(holder.contentPhoVid);
        }

        holder.tvTitle.setText(content.get(position).getTitle());
        holder.tvCreated.setText(content.get(position).getUpdated_at());


//
//        if(position > content.size() && position != content.size()){
//
//
//        }else {
//            position *=2;
//
//            Glide.with(context).load(content.get(position).getUrl()).into(holder.contentPhoVidFirst);

//            holder.nameFirst.setText(influencers.get(position).getFirst_name()+" "+influencers.get(position).getLast_name());
//                    Picasso.with(context).load(objects.get(position).get("ImageItem").toString()).into(holder.imgItem);
//                    holder.txt_desc.setText(""+objects.get(position).get("Description").toString());
//                    holder.txt_name.setText(""+objects.get(position).get("ItemName").toString());

           // final int finalPosition = position;

//                    holder.imgItem.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if(mobile.compareTo("")==0){
//                                Toast.makeText(context, "Please add phone number to proceed with add item (Profile->Edit Profile)", Toast.LENGTH_SHORT).show();
//                            }else {
//                                if (Constants.ObjectId.compareTo(objects.get(finalPosition).get("objectID").toString()) == 0) {
//
//                                } else {
//                                    Constants.itemObjectId = objects.get(finalPosition).getObjectId();
//                                    Constants.OwnerObjectID = objects.get(finalPosition).get("objectID").toString();
//                                    Intent intent = new Intent(context, PurchaseActivity.class);
//                                    context.startActivity(intent);
//                                }
//                            }
//
//                        }
//                    });

//            if(position + 1 != content.size()) {
//                        Picasso.with(context).load(objects.get(position + 1).get("ImageItem").toString()).into(holder.imgItem_1);
//                        holder.txt_desc_1.setText(""+objects.get(position + 1).get("Description").toString());
//                        holder.txt_name_1.setText(""+objects.get(position + 1).get("ItemName").toString());



//                final int finalPosition_1 = position + 1;

//
//                        holder.imgItem_1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if(mobile.compareTo("")==0){
//                                    Toast.makeText(context, "Please add phone number to proceed with add item (Profile->Edit Profile)", Toast.LENGTH_SHORT).show();
//                                }else {
//                                    if (Constants.ObjectId.compareTo(objects.get(finalPosition_1).get("objectID").toString()) == 0) {
//
//                                    } else {
//                                        Constants.itemObjectId = objects.get(finalPosition_1).getObjectId();
//                                        Constants.OwnerObjectID = objects.get(finalPosition_1).get("objectID").toString();
//                                        Intent intent = new Intent(context, PurchaseActivity.class);
//                                        context.startActivity(intent);
//                                    }
//                                }
//
//                            }
//                        });

//            }
//            else{
//                holder.conatainerFirst.setVisibility(View.GONE);
//                //Toast.makeText(context, "hi"+holder.imgItem_1.getWidth(), Toast.LENGTH_SHORT).show();
//                //Toast.makeText(context, ""+holder.imgItem.getWidth(), Toast.LENGTH_SHORT).show();
//            }
//        }

//        final int pos2 = position + 1;

        holder.contentPhoVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SponsorActivity.contentDescription = content.get(position).getDescription();
                SponsorActivity.contentPhotoUrl = content.get(position).getUrl();
                SponsorActivity.contentId = content.get(position).getId();
                Intent intent = new Intent();
                intent.setClass(context, ViewContentActivity.class);
                context.startActivity(intent);
            }
        });


//        holder.contentPhotoVidSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SponsorActivity.contentDescription = content.get(pos2).getDescription();
//                SponsorActivity.contentPhotoUrl = content.get(pos2).getUrl();
//                SponsorActivity.contentId = content.get(pos2).getId();
//                Intent intent = new Intent();
//                intent.setClass(context, ViewContentActivity.class);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return content.size();
    }


}
