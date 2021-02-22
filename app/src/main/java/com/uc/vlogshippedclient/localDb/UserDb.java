package com.uc.vlogshippedclient.localDb;

import android.util.Log;

import com.uc.vlogshippedclient.SponsorActivity;
import com.uc.vlogshippedclient.model.Users;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class UserDb {

    private static final String TAG = UserDb.class.getName();

    private UserDb() {

    }

    public static void setUser(final JSONObject userData) {
        Log.i(TAG, "Set user DB");
        Log.i(TAG, userData.toString());

        Realm mRealm = SponsorActivity.getRealm();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Users mUser = realm.createObject(Users.class);
                try {
                    mUser.setId(userData.getInt("user_id"));
                    mUser.setFirst_name(userData.getString("first_name"));
                    mUser.setLast_name(userData.getString("last_name"));
                    mUser.setMobile_number(userData.getString("mobile_number"));
                    mUser.setEmail_address(userData.getString("email_address"));
                    mUser.setCompanyName(userData.getString("company_name"));
                    mUser.setWebsite(userData.getString("website"));
                    mUser.setDescription(userData.getString("description"));
                    mUser.setProfile_picture(userData.getString("profile_picture"));

                    Log.i("ErrorRealm:", "" + mUser.toString());
                } catch (JSONException je) {
                    je.printStackTrace();
                    Log.i("ErrorRealm:", "" + je);
                }
            }
        });

    }

    public static void updateInfo(final JSONObject userData) {

        Log.i(TAG, "Set user DB");
        Log.i(TAG, userData.toString());

        Realm mRealm = SponsorActivity.getRealm();

        final Users toEdit = mRealm.where(Users.class).findFirst();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {

                    toEdit.setId(userData.getInt("user_id"));
                    toEdit.setFirst_name(userData.getString("first_name"));
                    toEdit.setLast_name(userData.getString("last_name"));
                    toEdit.setMobile_number(userData.getString("mobile_number"));
                    //toEdit.setEmail_address(userData.getString("email_address"));
                    toEdit.setCompanyName(userData.getString("company_name"));
                    toEdit.setWebsite(userData.getString("website"));
                    toEdit.setDescription(userData.getString("description"));
                    toEdit.setProfile_picture(userData.getString("profile_picture"));

                }catch (JSONException je){
                    Log.e("Error", ""+je);
                    je.printStackTrace();
                }
            }
        });
    }

    public static Users getUserAccount() {

        Realm mRealm = SponsorActivity.getRealm();
        Users mUser = mRealm.where(Users.class).findFirst();

        return mUser;
    }

    public static void clearDb() {
        Realm mRealm = SponsorActivity.getRealm();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Users.class);
            }
        });
    }

}
