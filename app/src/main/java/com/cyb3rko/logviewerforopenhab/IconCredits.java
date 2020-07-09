package com.cyb3rko.logviewerforopenhab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class IconCredits extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load save file
        SharedPreferences mySPR = this.getSharedPreferences("Safe", 0);

        // restore set orientation
        setRequestedOrientation(mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED));

        // set view
        setContentView(R.layout.activity_icon_credits);

        // set text of textviews and add links to authors
        TextView[] textViews = new TextView[9];
        String[] credits = getResources().getStringArray(R.array.credits_icons);
        String[] creditsLinks = getResources().getStringArray(R.array.credits_icons_links);

        for (int i = 1; i <= 9; i++) {
            // find textviews and set text
            textViews[i-1] = findViewById(getResources().getIdentifier("credits_icons_" + i, "id", getPackageName()));
            textViews[i-1].setText(getResources().getStringArray(R.array.credits_icons)[i-1]);

            // split string array items into link, authorAlias and authorName
            String link = creditsLinks[i-1].split(",")[0];
            String authorAlias = creditsLinks[i-1].split(",")[1].split(";")[0];
            String authorName = creditsLinks[i-1].split(",")[1].split(";")[1];

            // add clickable links
            setSpans(textViews[i-1], credits[i-1], link, credits[i-1].indexOf(authorAlias), credits[i-1].indexOf(authorAlias) + authorName.length());
        }
    }

    // method to add clickable links
    private void setSpans(TextView textView, String string, final String link, int startChar, int endChar) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        };

        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(clickableSpan, startChar, endChar != 0 ? endChar : string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
