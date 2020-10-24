package com.cyb3rko.logviewerforopenhab

import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet

internal val PRIVACY_POLICY = "privacy_policy"
internal val SHARED_PREFERENCE = "Safe2"
internal val TERMS_OF_USE = "terms_of_use"

internal fun showLicenseDialog(context: Context?, type: String) {
    MaterialDialog(context!!, BottomSheet()).show {
        message(0, Html.fromHtml(context.assets.open("$type.html").bufferedReader().use { it.readText() })) {
            messageTextView.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}