package com.thegreek.niko.logviewer_for_openHAB;

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
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect Firebase
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // load save file and its editor
        SharedPreferences mySPR = this.getSharedPreferences("Safe", 0);
        editor = mySPR.edit();
        editor.apply();

        // forbid queueing of toasts
        Toasty.Config.getInstance().allowQueue(false).apply();

        // open menu
        getSupportFragmentManager().beginTransaction().replace(R.id.start, new MainFragment()).commit();

        // open end user content dialog if it is not yet accepted
        if (mySPR.getBoolean("firstStart", true) || mySPR.getString("date", "").equals("")) {
            EndUserConsent.dialogType = true;
            EndUserConsent endUserConsent = new EndUserConsent();
            endUserConsent.setCancelable(false);
            endUserConsent.show(getSupportFragmentManager(), getClass().getName());
        } else {
            // enable Firebase Analytics
            firebaseAnalytics.setAnalyticsCollectionEnabled(true);
            // check for update
            updateCheck(this);
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
                        editor.putString("newestVersion", versionName);
                        editor.apply();

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
                        // nothing to clean up (for POM)
                    }
                });
    }

    // if back button pressed
    @Override
    public void onBackPressed() {
        finish();
    }
}
