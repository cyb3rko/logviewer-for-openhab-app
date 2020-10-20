package com.cyb3rko.logviewerforopenhab

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.webkit.URLUtil
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import es.dmoral.toasty.Toasty

class UpdateDialog : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // load save file and its editor
        val mySPR = activity?.getSharedPreferences("Safe", 0)
        val editor = mySPR?.edit()
        editor?.apply()

        // create new dialog builder
        val builder = AlertDialog.Builder(activity)

        // create title
        val titleView = TextView(context)
        titleView.setPadding(32, 32, 32, 32)
        titleView.gravity = Gravity.CENTER_HORIZONTAL
        titleView.typeface = Typeface.DEFAULT_BOLD
        titleView.textSize = 22f
        titleView.text = getString(R.string.update_dialog_title)

        // create text
        val messageView = TextView(context)
        messageView.setPadding(32, 32, 32, 32)
        messageView.gravity = Gravity.CENTER_HORIZONTAL
        messageView.textSize = 16f
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.update_changelog_link))))
            }
        }
        val message = String.format(getString(R.string.update_dialog_message), mySPR?.getString("newestVersion", ""), BuildConfig.VERSION_NAME)
        val index = getString(R.string.update_dialog_changelog)
        val spannableString = SpannableString(message)
        spannableString.setSpan(clickableSpan, message.indexOf(index), message.indexOf(index) + index.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        messageView.text = spannableString
        messageView.movementMethod = LinkMovementMethod.getInstance()

        // create dialog
        builder.setCustomTitle(titleView)
            .setView(messageView)
            .setCancelable(false) // add right button
            .setPositiveButton(getString(R.string.update_dialog_button_1)) { _, _ ->
                // if permission are given
                if (context?.let {
                        ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED) {
                    // create link
                    val link = String.format(getString(R.string.update_download_link), mySPR?.getString("newestVersion", ""))
                    // download apk file
                    val request = DownloadManager.Request(Uri.parse(link)).setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                            URLUtil.guessFileName(link, null, null)
                        )
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    val downloadManager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)
                } else {
                    // give error that permissions are not given
                    Toasty.error(context!!, getString(R.string.update_dialog_error), Toasty.LENGTH_LONG).show()
                }
            } // add left button
            .setNegativeButton(getString(R.string.update_dialog_button_2)) { _, _ ->
                // nothing to clean up (for PMD)
            }

        // show dialog
        return builder.create()
    }
}