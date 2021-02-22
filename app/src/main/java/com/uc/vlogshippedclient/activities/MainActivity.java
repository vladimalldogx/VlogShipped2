package com.uc.vlogshippedclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.uc.vlogshippedclient.R;
import com.uc.vlogshippedclient.localDb.UserDb;
import com.uc.vlogshippedclient.service.VlogshippedNotification;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private TextView tvFullName;
    private CircleImageView profilePicture;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TabHost tabHost, tabhostBelow;
    private LocalActivityManager localActivityManager;
    private TabWidget tabContent;

    private Intent notificationChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        localActivityManager = new LocalActivityManager(this, false);
        localActivityManager.dispatchCreate(savedInstanceState);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Sample", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("Message", token);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        setupviews();
        
    }





    private void setupviews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        tabHost = (TabHost) findViewById(R.id.tabhost);

        View navView =  navigationView.inflateHeaderView(R.layout.nav_header_drawer);
        profilePicture = navView.findViewById(R.id.profile_picture);
        tvFullName = navView.findViewById(R.id.tv_fullname);

        tvFullName.setText(UserDb.getUserAccount().getFirst_name()+" "+UserDb.getUserAccount().getLast_name());

        notificationChannel = new Intent(this, VlogshippedNotification.class);

        startService(notificationChannel);


        if(UserDb.getUserAccount().getProfile_picture().isEmpty() || UserDb.getUserAccount().getProfile_picture().compareToIgnoreCase("none") == 0){
            Glide.with(this).load(getResources().getDrawable(R.drawable.pp)).into(profilePicture);
        }else{
            Glide.with(this).load(UserDb.getUserAccount().getProfile_picture()).into(profilePicture);
        }

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        final Intent campaignActivity = new Intent().setClass(this, InfluencerListActivity.class);
        final Intent addCampaignActivity = new Intent().setClass(this, SelectionActivity.class);
        final Intent chat = new Intent().setClass(this, ChatHistoryActivity.class);
        tabHost.setup(localActivityManager);

        TabHost.TabSpec list =  tabHost.newTabSpec("List")
                .setIndicator("", getResources().getDrawable(R.drawable.homepage))
                .setContent(campaignActivity);

        TabHost.TabSpec add =  tabHost.newTabSpec("Add")
                .setIndicator("", getResources().getDrawable(R.drawable.add_black))
                .setContent(addCampaignActivity);

        TabHost.TabSpec email =  tabHost.newTabSpec("Email")
                .setIndicator("", getResources().getDrawable(R.drawable.email))
                .setContent(chat);

        tabHost.addTab(list);
        tabHost.addTab(add);
        tabHost.addTab(email);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_p) {
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_trans);
        } else if (id == R.id.nav_logout) {
            UserDb.clearDb();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }else if (id == R.id.nav_campaign) {
            Intent i = new Intent(MainActivity.this, CampaignActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_s) {
            Intent i = new Intent(MainActivity.this, ViewSubscriptionActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else if (id == R.id.nav_c) {

            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_trans);
        }else if (id == R.id.nav_set) {

            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_trans);
        }else if (id == R.id.nav_about) {

            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right_trans);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
