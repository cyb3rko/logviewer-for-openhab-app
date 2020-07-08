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
    private ClickableSpan clickableSpan1;
    private ClickableSpan clickableSpan2;
    private DialogInterface.OnClickListener dialogClickListener;
    private SharedPreferences mySPR;
    private SharedPreferences.Editor editor;
    private String title;
    private String button1Text;
    private SpannableString spannableString;

    public static boolean dialogType;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // load save file and its editor
        mySPR = Objects.requireNonNull(getActivity()).getSharedPreferences("Safe", 0);
        editor = mySPR.edit();
        editor.apply();

        // implement actions if click on link is registered
        clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                view.getContext().startActivity(new Intent(getContext(), PrivacyPolicy.class));
            }
        };
        clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                view.getContext().startActivity(new Intent(getContext(), TermsOfUse.class));
            }
        };

        // check which type of end user consent to show (either to accept or to show the already accepted end user consent)
        if (dialogType) {
            dialog1();
        } else {
            dialog2();
        }

        // create new dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // create title
        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setPadding(32, 32, 32, 32);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);

        // create text
        TextView messageView = new TextView(getContext());
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        messageView.setPadding(32, 32, 32, 32);
        messageView.setGravity(Gravity.CENTER_HORIZONTAL);
        messageView.setText(spannableString);
        messageView.setTextSize(16);

        // create dialog
        builder.setCustomTitle(titleView)
                .setView(messageView)
                // only allow to cancel on second dialog
                .setCancelable(!dialogType)
                // add right button
                .setPositiveButton(button1Text, dialogClickListener);

        // add left button if second dialog shall be shown
        if (dialogType) {
            builder.setNegativeButton(getString(R.string.end_user_consent_button_2), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Objects.requireNonNull(getActivity()).finish();
                }
            });
        }

        // show dialog
        return builder.create();
    }

    // set data of first dialog
    private void dialog1() {
        // set text
        String message = getString(R.string.end_user_consent_message);
        spannableString = new SpannableString(message);

        // add clickable links to policy and terms
        spannableString.setSpan(clickableSpan1, message.indexOf("Privacy"), message.indexOf("Privacy") + "Privacy Policy".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, message.indexOf("Terms"), message.indexOf("Terms") + "Terms of Use".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set title
        title = getString(R.string.end_user_consent_title);
        // set text of right button
        button1Text = getString(R.string.end_user_consent_button_1);
        // set what happens when right button is clicked
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // turn on Firebase Analytics
                MainActivity.firebaseAnalytics.setAnalyticsCollectionEnabled(true);
                // get date and time
                Date date = Calendar.getInstance().getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sDF = new SimpleDateFormat("dd.MM.yyyy");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sDF2 = new SimpleDateFormat("HH:mm:ss");
                // store dat and time
                editor.putString("date", sDF.format(date));
                editor.putString("time", sDF2.format(date));
                editor.putBoolean("firstStart", false).apply();
                // open menu
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace((R.id.start), new MainFragment()).commit();
            }
        };
    }

    // set data of second dialog
    private void dialog2() {
        // set text
        String message = getString(R.string.end_user_consent_2_message_1);
        message += mySPR.getString("date", "") + getString(R.string.end_user_consent_2_message_2) + mySPR.getString("time", "");
        spannableString = new SpannableString(message);

        // add clickable links to policy and terms
        spannableString.setSpan(clickableSpan1, message.indexOf("Privacy"), message.indexOf("Privacy") + "Privacy Policy".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, message.indexOf("Terms"), message.indexOf("Terms") + "Terms of Use".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set title
        title = getString(R.string.end_user_consent_2_title);
        // set text of right button
        button1Text = getString(R.string.end_user_consent_2_button);
        // set what happens when right button is clicked
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // nothing to clean up (for POM)
            }
        };
    }
}
