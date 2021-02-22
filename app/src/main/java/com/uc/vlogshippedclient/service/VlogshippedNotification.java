package com.uc.vlogshippedclient.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.common.images.ImageManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.activities.MainActivity;
import com.uc.vlogshippedclient.activities.NotificationActivity;
import com.uc.vlogshippedclient.activities.NotificationInviteActivity;
import com.uc.vlogshippedclient.activities.SplashActivity;
import com.uc.vlogshippedclient.localDb.UserDb;

public class VlogshippedNotification extends Service {

    private static final String TAG = "MyFirebaseMsgService";
    private PendingIntent contentIntent;
    private static final String NOTIFICATION_CHANNEL_NAME = "BLISS_SEARCH_BOOKING_CHANNEL_01";
    private static final String NOTIFICATION_CHANNEL_ID = "BLISS_SEARCH_BOOKING_01";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    public void onCreate() {

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Notification").child(String.valueOf(UserDb.getUserAccount().getId()));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() || dataSnapshot.getChildrenCount()  != 0){
                   for(DataSnapshot childkey : dataSnapshot.getChildren()){

                       if(childkey.child("NotificationStatus").getValue().toString().compareToIgnoreCase("2")==0){

                           //Toast.makeText(VlogshippedNotification.this, ""+childkey.child("NotificationID").getValue(), Toast.LENGTH_SHORT).show();

                            String tittle = "Vlogshipped";
                            String ContextText = "You have new Application on your campaign please click to view further information";
                            Intent notificationIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                            notificationIntent.putExtra("notification_id", childkey.child("NotificationID").getValue().toString());
                            notificationIntent.putExtra("campaign_id", childkey.child("NotificationTypeID").getValue().toString());
                            notificationIntent.putExtra("notification_from", childkey.child("NotificationFrom").getValue().toString());
                            notificationIntent.putExtra("key", childkey.getKey());
                            shownotification(tittle, ContextText, notificationIntent);

                        }else if(childkey.child("NotificationStatus").getValue().toString().compareToIgnoreCase("3")==0){

                           String tittle = "Vlogshipped";
                           String ContextText = "Your Invitation has been Approved click the notification for further information.";
                           Intent notificationIntent = new Intent(getApplicationContext(), NotificationInviteActivity.class);
                           notificationIntent.putExtra("notification_id", childkey.child("NotificationID").getValue().toString());
                           notificationIntent.putExtra("campaign_id", childkey.child("NotificationTypeID").getValue().toString());
                           notificationIntent.putExtra("notification_from", childkey.child("NotificationFrom").getValue().toString());
                           notificationIntent.putExtra("key", childkey.getKey());
                           shownotification(tittle, ContextText, notificationIntent);

                        }
                   }
//
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();


        //startForeground(R.string.noti_id, mBuilder);
    }

    private void shownotification(String tittle, String contextText, Intent notificationIntent) {


        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,   PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        //updateNotification(false);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.locker);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.locker)
                        .setContentTitle(tittle)
                        .setContentText(contextText)
                        .setPriority(2)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false));

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setLightColor(Color.BLUE);
                channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
                channel.setSound(null, null);
                channel.setVibrationPattern(null);
                channel.enableVibration(false);
                channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            } catch (Exception e) {
                Log.e("Noti Error", ""+e);

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
