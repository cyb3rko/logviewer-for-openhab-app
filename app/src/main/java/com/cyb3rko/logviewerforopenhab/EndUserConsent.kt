package com.cyb3rko.logviewerforopenhab
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.SharedPreferences
//import android.graphics.Typeface
//import android.os.Bundle
//import android.text.SpannableString
//import android.text.Spanned
//import android.text.method.LinkMovementMethod
//import android.text.style.ClickableSpan
//import android.view.Gravity
//import android.view.View
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatDialogFragment
//import java.text.SimpleDateFormat
//import java.util.*
//
//class EndUserConsent(val dialogType: Boolean) : AppCompatDialogFragment() {
//
//    private lateinit var clickableSpan1: ClickableSpan
//    private lateinit var clickableSpan2: ClickableSpan
//    private lateinit var dialogClickListener: DialogInterface.OnClickListener
//    private lateinit var mySPR: SharedPreferences
//    private lateinit var editor: SharedPreferences.Editor
//    private lateinit var title: String
//    private lateinit var button1Text: String
//    private lateinit var spannableString: SpannableString
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        // load save file and its editor
//        mySPR = activity!!.getSharedPreferences("Safe", 0)
//        editor = mySPR.edit()
//        editor.apply()
//
//        // implement actions if click on link is registered
//        clickableSpan1 = object : ClickableSpan() {
//            override fun onClick(view: View) {
//                view.context.startActivity(Intent(context, PrivacyPolicyFragment::class.java))
//            }
//        }
//        clickableSpan2 = object : ClickableSpan() {
//            override fun onClick(view: View) {
//                view.context.startActivity(Intent(context, TermsOfUseFragment::class.java))
//            }
//        }
//
//        // check which type of end user consent to show (either to accept or to show the already accepted end user consent)
//        if (dialogType) {
//            dialog1()
//        } else {
//            dialog2()
//        }
//
//        // create new dialog builder
//        val builder = AlertDialog.Builder(activity)
//
//        // create title
//        val titleView = TextView(context)
//        titleView.text = title
//        titleView.textSize = 22f
//        titleView.setTypeface(Typeface.DEFAULT_BOLD)
//        titleView.setPadding(32, 32, 32, 32)
//        titleView.gravity = Gravity.CENTER_HORIZONTAL
//
//        // create text
//        val messageView = TextView(context)
//        messageView.movementMethod = LinkMovementMethod.getInstance()
//        messageView.setPadding(32, 32, 32, 32)
//        messageView.gravity = Gravity.CENTER_HORIZONTAL
//        messageView.text = spannableString
//        messageView.textSize = 16f
//
//        // create dialog
//        builder.setCustomTitle(titleView)
//            .setView(messageView) // only allow to cancel on second dialog
//            .setCancelable(!dialogType) // add right button
//            .setPositiveButton(button1Text, dialogClickListener)
//
//        // add left button if second dialog shall be shown
//        if (dialogType) {
//            builder.setNegativeButton(getString(R.string.end_user_consent_button_2)) { _, _ ->
//                activity!!.finish()
//            }
//        }
//
//        // show dialog
//        return builder.create()
//    }
//
//    // set data of first dialog
//    private fun dialog1() {
//        // set text
//        val message = getString(R.string.end_user_consent_message)
//        spannableString = SpannableString(message)
//
//        // add clickable links to policy and terms
//        spannableString.setSpan(clickableSpan1, message.indexOf("Privacy"), message.indexOf("Privacy") + "Privacy Policy".length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        spannableString.setSpan(clickableSpan2, message.indexOf("Terms"), message.indexOf("Terms") + "Terms of Use".length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//
//        // set title
//        title = getString(R.string.end_user_consent_title)
//        // set text of right button
//        button1Text = getString(R.string.end_user_consent_button_1)
//        // set what happens when right button is clicked
//        dialogClickListener = DialogInterface.OnClickListener { _, _ -> // turn on Firebase Analytics
//            MainActivity.firebaseAnalytics.setAnalyticsCollectionEnabled(true)
//            // get date and time
//            val date = Calendar.getInstance().time
//            @SuppressLint("SimpleDateFormat") val sDF = SimpleDateFormat("dd.MM.yyyy")
//            @SuppressLint("SimpleDateFormat") val sDF2 = SimpleDateFormat("HH:mm:ss")
//            // store dat and time
//            editor.putString("date", sDF.format(date))
//            editor.putString("time", sDF2.format(date))
//            editor.putBoolean("firstStart", false).apply()
//            // open menu
//            editor.putInt("orientation", -1).apply()
//            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.start, MainFragment())?.commit()
//        }
//    }
//
//    // set data of second dialog
//    private fun dialog2() {
//        // set text
//        var message = getString(R.string.end_user_consent_2_message_1)
//        message += mySPR.getString("date", "") + getString(R.string.end_user_consent_2_message_2) + mySPR.getString("time", "")
//        spannableString = SpannableString(message)
//
//        // add clickable links to policy and terms
//        spannableString.setSpan(clickableSpan1, message.indexOf("Privacy"), message.indexOf("Privacy") + "Privacy Policy".length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        spannableString.setSpan(clickableSpan2, message.indexOf("Terms"), message.indexOf("Terms") + "Terms of Use".length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//
//        // set title
//        title = getString(R.string.end_user_consent_2_title)
//        // set text of right button
//        button1Text = getString(R.string.end_user_consent_2_button)
//        // set what happens when right button is clicked
//        dialogClickListener = DialogInterface.OnClickListener { _, _ ->
//            // nothing to clean up (for PMD)
//        }
//    }
//}