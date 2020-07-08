package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PrivacyPolicy extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load save file
        SharedPreferences mySPR = this.getSharedPreferences("Safe", 0);
        // restore set orientation
        setRequestedOrientation(mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED));
        // set view
        setContentView(R.layout.activity_privacy_policy);

        // set text of textviews
        TextView[] textViews = new TextView[21];

        for (int i = 1; i <= 21; i++) {
            textViews[i-1] = findViewById(getResources().getIdentifier("textView" + i, "id", getPackageName()));
            textViews[i-1].setText(getResources().getStringArray(R.array.privacy_policy)[i-1]);
        }

        // floating action button clicklistener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start intent for writing an e mail
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse(String.format(getString(R.string.mail_info), getString(R.string.app_name), getResources().getStringArray(R.array.privacy_policy)[0]));
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, getString(R.string.send_mail)));
            }
        });
    }
}
