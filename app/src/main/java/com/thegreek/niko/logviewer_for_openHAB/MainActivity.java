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

public class MainActivity extends AppCompatActivity {

    static FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences mySPR;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mySPR = this.getSharedPreferences("Speicherstand", 0);
        editor = mySPR.edit();
        editor.apply();

        getSupportFragmentManager().beginTransaction().replace(R.id.start, new MainFragment()).commit();

        if (mySPR.getBoolean("firstStart", true)) {
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
                        editor.putString("neuesteVersion", parts2[0]);
                        editor.apply();

                        if (BuildConfig.VERSION_CODE != neuesterVersionCode) {
                            System.out.println("----------\nUpdate verfügbar: " + mySPR.getString("neuesteVersion", "") + "\n----------");

                            UpdateDialog updateDialog = new UpdateDialog();
                            updateDialog.setCancelable(false);
                            updateDialog.show(getSupportFragmentManager(), "Update-Dialog");

                            ActivityCompat.requestPermissions(activity , new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
                        } else {
                            System.out.println("----------\nApp auf dem neuesten Stand\n----------");
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("----------\nUpdate-Abfrage fehlgeschlagen: " + anError.getErrorBody() + "\n----------");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
