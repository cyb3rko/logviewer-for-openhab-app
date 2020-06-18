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
        TextView textView5 = findViewById(R.id.credits5);
        TextView textView6 = findViewById(R.id.credits6);
        TextView textView7 = findViewById(R.id.credits7);
        TextView textView8 = findViewById(R.id.credits8);
        TextView textView9 = findViewById(R.id.credits9);
        TextView textView11 = findViewById(R.id.credits11);
        TextView textView12 = findViewById(R.id.credits12);
        TextView textView13 = findViewById(R.id.credits13);
        TextView textView14 = findViewById(R.id.credits14);
        TextView textView15 = findViewById(R.id.credits15);
        TextView textView16 = findViewById(R.id.credits16);

        setSpans(textView1, getString(R.string.credits1), "https://nikothegreek.jimdofree.com", 28, 38);
        setSpans(textView2, getString(R.string.credits2), "https://developer.android.com/studio", 5, 0);
        setSpans(textView3, getString(R.string.credits3), "https://nikothegreek.jimdofree.com", 33, 43);
        setSpans(textView4, getString(R.string.credits4), "https://gimp.org", 5, 0);
        setSpans(textView5, getString(R.string.credits5), "https://www.flaticon.com/authors/dave-gandy", 24, 34);
        setSpans(textView6, getString(R.string.credits6), "https://www.flaticon.com/authors/smartline", 21, 30);
        setSpans(textView7, getString(R.string.credits7), "https://www.flaticon.com/authors/freepik", 20, 27);
        setSpans(textView8, getString(R.string.credits8), "https://www.flaticon.com/authors/those-icons", 20, 31);
        setSpans(textView9, getString(R.string.credits9), "https://www.flaticon.com/authors/lyolya", 26, 32);
        setSpans(textView11, getString(R.string.credits11), "https://github.com/AnderWeb/discreteSeekBar", 31, 40);
        setSpans(textView12, getString(R.string.credits12), "https://apache.org/licenses/LICENSE-2.0", 20, 32);
        setSpans(textView13, getString(R.string.credits13), "https://github.com/KeepSafe/TapTargetView", 29, 38);
        setSpans(textView14, getString(R.string.credits14), "https://apache.org/licenses/LICENSE-2.0", 20, 32);
        setSpans(textView15, getString(R.string.credits15), "https://github.com/GrenderG/Toasty", 22, 31);
        setSpans(textView16, getString(R.string.credits16), "https://www.gnu.org/licenses/lgpl-3.0.html", 18, 30);
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
