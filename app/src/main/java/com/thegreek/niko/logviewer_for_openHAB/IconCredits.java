package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Intent;
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
        setContentView(R.layout.activity_icon_credits);

        TextView[] textViews = new TextView[9];

        for (int i = 1; i <= 9; i++) {
            textViews[i-1] = findViewById(getResources().getIdentifier("credits_icons_" + i, "id", getPackageName()));
        }

        setSpans(textViews[0], getString(R.string.credits_icons_1), getString(R.string.credits_icons_links_5), 24, 34);
        setSpans(textViews[1], getString(R.string.credits_icons_2), getString(R.string.credits_icons_links_4), 21, 30);
        setSpans(textViews[2], getString(R.string.credits_icons_3), getString(R.string.credits_icons_links_2), 20, 31);
        setSpans(textViews[3], getString(R.string.credits_icons_4), getString(R.string.credits_icons_links_2), 20, 31);
        setSpans(textViews[4], getString(R.string.credits_icons_5), getString(R.string.credits_icons_links_3), 26, 32);
        setSpans(textViews[5], getString(R.string.credits_icons_6), getString(R.string.credits_icons_links_2), 24, 35);
        setSpans(textViews[6], getString(R.string.credits_icons_7), getString(R.string.credits_icons_links_1), 27, 34);
        setSpans(textViews[7], getString(R.string.credits_icons_8), getString(R.string.credits_icons_links_1), 20, 27);
        setSpans(textViews[8], getString(R.string.credits_icons_9), getString(R.string.credits_icons_links_1), 24, 31);
    }

    void setSpans(TextView textView, String string, final String link, int startChar, int endChar) {
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
