package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PrivacyPolicy extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + getString(R.string.app_name) + " - Privacy Policy"+ "&body=" + "" + "&to=" + "nikodiamond3@gmail.com");
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, "Sende eine Mail an mich..."));
            }
        });
    }
}
