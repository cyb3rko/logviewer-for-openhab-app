package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class TermsOfUse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mySPR = this.getSharedPreferences("Safe", 0);
        MainActivity.changeOrientation(Objects.requireNonNull(this), mySPR.getInt("orientation", 0));
        setContentView(R.layout.activity_terms_of_use);

        TextView[] textViews = new TextView[7];

        for (int i = 1; i <= 7; i++) {
            textViews[i-1] = findViewById(getResources().getIdentifier("textView" + i, "id", getPackageName()));
            textViews[i-1].setText(getResources().getStringArray(R.array.terms_of_use)[i-1]);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + getString(R.string.app_name) + " - Terms of Use"+ "&body=" + "" + "&to=" + "nikodiamond3@gmail.com");
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, getString(R.string.send_mail)));
            }
        });
    }
}
