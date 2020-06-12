package com.thegreek.niko.logviewer_for_openHAB;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    static FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        SharedPreferences mySPR = this.getSharedPreferences("Speicherstand", 0);

        getSupportFragmentManager().beginTransaction().replace(R.id.start, new MainFragment()).commit();

        if (mySPR.getBoolean("firstStart", true)) {
            EndUserConsent endUserConsent = new EndUserConsent();
            endUserConsent.setCancelable(false);
            endUserConsent.show(getSupportFragmentManager(), "Endnutzer-Einwilligung");
        } else {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
