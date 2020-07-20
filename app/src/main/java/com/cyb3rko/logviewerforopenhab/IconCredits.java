package com.cyb3rko.logviewerforopenhab;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cyb3rko.abouticons.AboutIcons;

public class IconCredits extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load save file
        SharedPreferences mySPR = this.getSharedPreferences("Safe", 0);

        // restore set orientation
        setRequestedOrientation(mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED));

        // set view
        setContentView(new AboutIcons(this, R.drawable.class).get());
    }
}
