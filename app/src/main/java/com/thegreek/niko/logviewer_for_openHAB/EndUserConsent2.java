package com.thegreek.niko.logviewer_for_openHAB;

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

public class EndUserConsent2 extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences mySPR = getActivity().getSharedPreferences("Speicherstand", 0);
        final SharedPreferences.Editor editor = mySPR.edit();
        editor.apply();

        String message = getString(R.string.end_user_consent2_message_1);

        message += mySPR.getString("datum", "") + getString(R.string.end_user_consent2_message_2) + mySPR.getString("uhrzeit", "");

        SpannableString spannableString = new SpannableString(message);

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

        spannableString.setSpan(clickableSpan1, message.indexOf("Privacy"), message.indexOf("Privacy") + "Pricacy Policy".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, message.indexOf("Terms"), message.indexOf("Terms") + "Terms of Use".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        TextView titleView = new TextView(getContext());
        titleView.setText(getString(R.string.end_user_consent2_title));
        titleView.setTextSize(22);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setPadding(32, 32, 32, 32);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView messageView = new TextView(getContext());
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        messageView.setPadding(32, 32, 32, 32);
        messageView.setGravity(Gravity.CENTER_HORIZONTAL);
        messageView.setText(spannableString);
        messageView.setTextSize(16);
        builder.setView(messageView)
                .setCustomTitle(titleView)
                .setPositiveButton(getString(R.string.end_user_consent2_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return builder.create();
    }
}
