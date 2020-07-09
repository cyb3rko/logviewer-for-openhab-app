package com.cyb3rko.logviewerforopenhab;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    public static FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences mySPR;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect Firebase
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // load save file and its editor
        mySPR = this.getSharedPreferences("Safe", 0);
        editor = mySPR.edit();
        editor.apply();

        // forbid queueing of toasts
        Toasty.Config.getInstance().allowQueue(false).apply();

        // open end user content dialog if it is not yet accepted
        if (mySPR.getBoolean("firstStart", true) || mySPR.getString("date", "").equals("")) {
            EndUserConsent.dialogType = true;
            EndUserConsent endUserConsent = new EndUserConsent();
            endUserConsent.setCancelable(false);
            endUserConsent.show(getSupportFragmentManager(), getClass().getName());
        } else {
            // enable Firebase Analytics
            firebaseAnalytics.setAnalyticsCollectionEnabled(true);
            // open menu
            getSupportFragmentManager().beginTransaction().replace(R.id.start, new MainFragment()).commit();
            // check if orientation was recently changed
            if (!mySPR.getBoolean("tempDisableStart", false)) {
                // check for update
                updateCheck(this);
            }
        }
    }

    // method to check for updates and open update dialog if new update is available
    private void updateCheck(final Activity activity) {
        AndroidNetworking.get(getString(R.string.update_check_link))
                .doNotCacheResponse()
                .build()
                .getAsString(new StringRequestListener() {
                    // if request is succesful
                    @Override
                    public void onResponse(String response) {
                        // extract and store newest version code and name
                        String versionCodeAndFollowing = response.split("versionCode ")[1];
                        String versionCode = versionCodeAndFollowing.split("\n")[0];
                        int newestVersionCode = Integer.parseInt(versionCode);
                        String versionNameAndFollowing = versionCodeAndFollowing.split("\"")[1];
                        String versionName = versionNameAndFollowing.split("\"")[0];
                        editor.putString("newestVersion", versionName).apply();

                        // if newer update available, open update dialog
                        if (BuildConfig.VERSION_CODE != newestVersionCode) {
                            UpdateDialog updateDialog = new UpdateDialog();
                            updateDialog.show(getSupportFragmentManager(), getClass().getName());

                            // request permissions
                            ActivityCompat.requestPermissions(activity , new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
                        }
                    }
                    // if request is not succesful
                    @Override
                    public void onError(ANError anError) {
                        // nothing to clean up (for PMD)
                    }
                });
    }

    // on app stop
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if log was shown
        if (!mySPR.getBoolean("connected", false)) {
            // set tempDisableStart to true to open log view again after orientation was changed
            editor.putBoolean("tempDisableStart", true).apply();
        }
    }

    // if back button pressed
    @Override
    public void onBackPressed() {
        finish();
    }
}
