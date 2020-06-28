package com.thegreek.niko.logviewer_for_openHAB;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class EndUserConsent extends AppCompatDialogFragment {

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences mySPR = Objects.requireNonNull(getActivity()).getSharedPreferences("Safe", 0);
        final SharedPreferences.Editor editor = mySPR.edit();

        SpannableString ss = new SpannableString(getString(R.string.end_user_consent_message));

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                view.getContext().startActivity(new Intent(getContext(), PrivacyPolicy.class));
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                view.getContext().startActivity(new Intent(getContext(), TermsOfUse.class));
            }
        };

        ss.setSpan(clickableSpan1, 35, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 58, 70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        TextView titleView = new TextView(getContext());
        titleView.setText(getString(R.string.end_user_consent_title));
        titleView.setTextSize(22);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setPadding(32, 32, 32, 32);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView messageView = new TextView(getContext());
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        messageView.setPadding(32, 32, 32, 32);
        messageView.setGravity(Gravity.CENTER_HORIZONTAL);
        messageView.setText(ss);
        messageView.setTextSize(16);
        builder.setView(messageView)
                .setCustomTitle(titleView)
                .setPositiveButton(getString(R.string.end_user_consent_button_1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.firebaseAnalytics.setAnalyticsCollectionEnabled(true);
                        Date date = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDF = new SimpleDateFormat("dd.MM.yyyy");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDF2 = new SimpleDateFormat("HH:mm:ss");
                        editor.putString("date", sDF.format(date));
                        editor.putString("time", sDF2.format(date));
                        editor.putBoolean("firstStart", false).apply();
                    }
                })
                .setNegativeButton(getString(R.string.end_user_consent_button_2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Objects.requireNonNull(getActivity()).finish();
                    }
                });

        return builder.create();
    }
}
