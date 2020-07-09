package com.cyb3rko.logviewerforopenhab;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpdateDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // load save file and its editor
        final SharedPreferences mySPR = Objects.requireNonNull(getActivity()).getSharedPreferences("Safe", 0);
        final SharedPreferences.Editor editor = mySPR.edit();
        editor.apply();

        // create new dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // create title
        TextView titleView = new TextView(getContext());
        titleView.setPadding(32, 32, 32, 32);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setTextSize(22);
        titleView.setText(getString(R.string.update_dialog_title));

        // create text
        TextView messageView = new TextView(getContext());
        messageView.setPadding(32, 32, 32, 32);
        messageView.setGravity(Gravity.CENTER_HORIZONTAL);
        messageView.setTextSize(16);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.update_changelog_link))));
            }
        };
        String message = String.format(getString(R.string.update_dialog_message), mySPR.getString("newestVersion", ""), BuildConfig.VERSION_NAME);
        String index = getString(R.string.update_dialog_changelog);
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(clickableSpan, message.indexOf(index), message.indexOf(index) + index.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        messageView.setText(spannableString);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());

        // create dialog
        builder.setCustomTitle(titleView)
                .setView(messageView)
                .setCancelable(false)
                // add right button
                .setPositiveButton(getString(R.string.update_dialog_button_1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // if permission are given
                        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // create link
                            String link = String.format(getString(R.string.update_download_link), mySPR.getString("newestVersion", ""));
                            // download apk file
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link))
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(link, null, null))
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            DownloadManager downloadManager = (DownloadManager) Objects.requireNonNull(getActivity()).getSystemService(DOWNLOAD_SERVICE);
                            assert downloadManager != null;
                            downloadManager.enqueue(request);
                        } else {
                            // give error that permissions are not given
                            Toasty.error(getContext(), getString(R.string.update_dialog_error), Toasty.LENGTH_LONG).show();
                        }
                    }
                })
                // add left button
                .setNegativeButton(getString(R.string.update_dialog_button_2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing to clean up (for PMD)
                    }
                });

        // show dialog
        return builder.create();
    }
}
