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

        TextView textView5 = findViewById(R.id.credits_icons_1);
        TextView textView6 = findViewById(R.id.credits_icons_2);
        TextView textView7 = findViewById(R.id.credits_icons_3);
        TextView textView8 = findViewById(R.id.credits_icons_4);
        TextView textView9 = findViewById(R.id.credits_icons_5);
        TextView textView10 = findViewById(R.id.credits_icons_6);
        TextView textView11 = findViewById(R.id.credits_icons_7);
        TextView textView12 = findViewById(R.id.credits_icons_8);
        TextView textView13 = findViewById(R.id.credits_icons_9);

        setSpans(textView5, getString(R.string.credits_icons_1), "https://www.flaticon.com/authors/dave-gandy", 24, 34);
        setSpans(textView6, getString(R.string.credits_icons_2), "https://www.flaticon.com/authors/smartline", 21, 30);
        setSpans(textView7, getString(R.string.credits_icons_3), "https://www.flaticon.com/authors/those-icons", 20, 31);
        setSpans(textView8, getString(R.string.credits_icons_4), "https://www.flaticon.com/authors/those-icons", 20, 31);
        setSpans(textView9, getString(R.string.credits_icons_5), "https://www.flaticon.com/authors/lyolya", 26, 32);
        setSpans(textView10, getString(R.string.credits_icons_6), "https://www.flaticon.com/authors/those-icons", 24, 35);
        setSpans(textView11, getString(R.string.credits_icons_7), "https://www.flaticon.com/authors/freepik", 27, 34);
        setSpans(textView12, getString(R.string.credits_icons_8), "https://www.flaticon.com/authors/freepik", 20, 27);
        setSpans(textView13, getString(R.string.credits_icons_9), "https://www.flaticon.com/authors/freepik", 24, 31);
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
