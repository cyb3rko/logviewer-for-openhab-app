package com.cyb3rko.logviewerforopenhab

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.google.firebase.analytics.FirebaseAnalytics
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {

    private lateinit var mySPR: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect Firebase
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // load save file and its editor
        mySPR = getSharedPreferences("Safe", 0)
        editor = mySPR.edit()
        editor.apply()

        // forbid queueing of toasts
        Toasty.Config.getInstance().allowQueue(false).apply()

        // open end user content dialog if it is not yet accepted
        if (mySPR.getBoolean("firstStart", true) || mySPR.getString("date", "") == "") {
            val endUserConsent = EndUserConsent(true)
            endUserConsent.isCancelable = false
            endUserConsent.show(supportFragmentManager, javaClass.name)
        } else {
            // enable Firebase Analytics
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            // open menu
            supportFragmentManager.beginTransaction().replace(R.id.start, MainFragment()).commit()
            // check if orientation was recently changed
            if (!mySPR.getBoolean("tempDisableStart", false)) {
                // check for update
                updateCheck(this)
            }
        }
    }

    private fun updateCheck(activity: Activity) {
        AndroidNetworking.get(getString(R.string.update_check_link))
            .doNotCacheResponse()
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String) {
                    val versionCodeAndFollowing = response.split("versionCode ")[1]
                    val versionCode = versionCodeAndFollowing.split("\n")[0]
                    val newestVersionCode = versionCode.toInt()
                    val versionNameAndFollowing = versionCodeAndFollowing.split("\"")[1]
                    val versionName = versionNameAndFollowing.split("\"")[0]
                    editor.putString("newestVersion", versionName).apply()

                    if (BuildConfig.VERSION_CODE != newestVersionCode) {
                        val dialogMessage = String.format(getString(R.string.update_dialog_message), mySPR.getString("newestVersion", ""),
                            BuildConfig.VERSION_NAME)

                        MaterialDialog(this@MainActivity).show {
                            title(R.string.update_dialog_title)
                            message(0, dialogMessage)
                            positiveButton(R.string.update_dialog_button_1) {
                                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    downloadNewestApk(applicationContext, mySPR.getString("newestVersion", "")!!)
                                } else {
                                    Toasty.error(context, getString(R.string.update_dialog_error), Toasty.LENGTH_LONG).show()
                                }
                            }
                            negativeButton(R.string.update_dialog_button_2) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.update_changelog_link))))
                            }
                        }

                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES
                            ), 1
                        )
                    }
                }

                override fun onError(anError: ANError) {
                    // nothing to clean up (for PMD)
                }
            })
    }

    // on app stop
    override fun onDestroy() {
        super.onDestroy()
        // if log was shown
        if (!mySPR.getBoolean("connected", false)) {
            // set tempDisableStart to true to open log view again after orientation was changed
            editor.putBoolean("tempDisableStart", true).apply()
        }
    }

    // if back button pressed
    override fun onBackPressed() {
        finish()
    }

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
    }
}