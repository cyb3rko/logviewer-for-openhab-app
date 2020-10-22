package com.cyb3rko.logviewerforopenhab

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil

internal fun downloadNewestApk(context: Context, version: String) {
    val link = String.format(context.getString(R.string.update_download_link), version)

    val request = DownloadManager.Request(Uri.parse(link)).setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
        URLUtil.guessFileName(link, null, null))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}

internal fun setToolbarVisibility(activity: Activity?, visibility: Int) {
    activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility = visibility
}