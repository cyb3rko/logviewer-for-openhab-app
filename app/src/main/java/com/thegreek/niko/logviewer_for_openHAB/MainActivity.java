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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SharedPreferences mySPR = this.getSharedPreferences("Safe", 0);
        editor = mySPR.edit();
        editor.apply();
//        Toasty.Config.getInstance().allowQueue(false).apply();
        MainActivity.changeOrientation(Objects.requireNonNull(this), mySPR.getInt("orientation", 0));

        getSupportFragmentManager().beginTransaction().replace(R.id.start, new MainFragment()).commit();

        if (mySPR.getBoolean("firstStart", true) || mySPR.getString("date", "").equals("")) {
            EndUserConsent endUserConsent = new EndUserConsent();
            endUserConsent.setCancelable(false);
            endUserConsent.show(getSupportFragmentManager(), "Endnutzer-Einwilligung");
        } else {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        }

        updateCheck(this);
    }

    private void updateCheck(final Activity activity) {
        AndroidNetworking.get("https://raw.githubusercontent.com/nikothegreek/logviewer-for-openhab-app/master/app/build.gradle")
                .doNotCacheResponse()
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        String[] parts = response.split("versionCode ");
                        String[] parts2 = parts[1].split("\n");
                        int neuesterVersionCode = Integer.parseInt(parts2[0]);
                        parts = parts2[1].split("\"");
                        parts2 = parts[1].split("\"");
                        editor.putString("newestVersion", parts2[0]);
                        editor.apply();

                        if (BuildConfig.VERSION_CODE != neuesterVersionCode) {
                            UpdateDialog updateDialog = new UpdateDialog();
                            updateDialog.setCancelable(false);
                            updateDialog.show(getSupportFragmentManager(), "Update-Dialog");

                            ActivityCompat.requestPermissions(activity , new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    static void changeOrientation(Activity activity, int orientation) {
        activity.setRequestedOrientation(orientation);
    }
}
