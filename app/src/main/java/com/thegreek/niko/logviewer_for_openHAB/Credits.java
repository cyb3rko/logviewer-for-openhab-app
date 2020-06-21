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

public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        TextView textView1 = findViewById(R.id.credits1);
        TextView textView2 = findViewById(R.id.credits2);
        TextView textView3 = findViewById(R.id.credits3);
        TextView textView4 = findViewById(R.id.credits4);
        TextView textView5 = findViewById(R.id.credits_icons_1);
        TextView textView6 = findViewById(R.id.credits_icons_2);
        TextView textView7 = findViewById(R.id.credits_icons_3);
        TextView textView8 = findViewById(R.id.credits_icons_4);
        TextView textView9 = findViewById(R.id.credits_icons_5);
        TextView textView10 = findViewById(R.id.credits_icons_6);
        TextView textView11 = findViewById(R.id.credits_libraries_1);
        TextView textView12 = findViewById(R.id.credits_libraries_2);
        TextView textView13 = findViewById(R.id.credits_libraries_3);
        TextView textView14 = findViewById(R.id.credits_libraries_4);
        TextView textView15 = findViewById(R.id.credits_libraries_5);
        TextView textView16 = findViewById(R.id.credits_libraries_6);
        TextView textView17 = findViewById(R.id.credits_libraries_7);
        TextView textView18 = findViewById(R.id.credits_libraries_8);

        setSpans(textView1, getString(R.string.credits1), "https://nikothegreek.jimdofree.com", 28, 38);
        setSpans(textView2, getString(R.string.credits2), "https://developer.android.com/studio", 5, 0);
        setSpans(textView3, getString(R.string.credits3), "https://nikothegreek.jimdofree.com", 33, 43);
        setSpans(textView4, getString(R.string.credits4), "https://gimp.org", 5, 0);
        setSpans(textView5, getString(R.string.credits_icons_1), "https://www.flaticon.com/authors/dave-gandy", 24, 34);
        setSpans(textView6, getString(R.string.credits_icons_2), "https://www.flaticon.com/authors/smartline", 21, 30);
        setSpans(textView7, getString(R.string.credits_icons_3), "https://www.flaticon.com/authors/those-icons", 20, 31);
        setSpans(textView8, getString(R.string.credits_icons_4), "https://www.flaticon.com/authors/those-icons", 20, 31);
        setSpans(textView9, getString(R.string.credits_icons_5), "https://www.flaticon.com/authors/lyolya", 26, 32);
        setSpans(textView10, getString(R.string.credits_icons_6), "https://www.flaticon.com/authors/those-icons", 24, 35);
        setSpans(textView11, getString(R.string.credits_libraries_1), "https://github.com/AnderWeb/discreteSeekBar", 31, 40);
        setSpans(textView12, getString(R.string.credits_libraries_2), "https://apache.org/licenses/LICENSE-2.0", 20, 32);
        setSpans(textView13, getString(R.string.credits_libraries_3), "https://github.com/KeepSafe/TapTargetView", 29, 38);
        setSpans(textView14, getString(R.string.credits_libraries_4), "https://apache.org/licenses/LICENSE-2.0", 20, 32);
        setSpans(textView15, getString(R.string.credits_libraries_5), "https://github.com/GrenderG/Toasty", 22, 31);
        setSpans(textView16, getString(R.string.credits_libraries_6), "https://www.gnu.org/licenses/lgpl-3.0.html", 18, 30);
        setSpans(textView17, getString(R.string.credits_libraries_7), "https://github.com/amitshekhariitbhu/Fast-Android-Networking", 43, 52);
        setSpans(textView18, getString(R.string.credits_libraries_8), "https://apache.org/licenses/LICENSE-2.0", 20, 32);
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
