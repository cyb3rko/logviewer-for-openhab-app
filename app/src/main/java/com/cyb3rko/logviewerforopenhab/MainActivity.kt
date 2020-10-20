package com.cyb3rko.logviewerforopenhab

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

    // method to check for updates and open update dialog if new update is available
    private fun updateCheck(activity: Activity) {
        AndroidNetworking.get(getString(R.string.update_check_link))
            .doNotCacheResponse()
            .build()
            .getAsString(object : StringRequestListener {
                // if request is succesful
                override fun onResponse(response: String) {
                    // extract and store newest version code and name
                    val versionCodeAndFollowing = response.split("versionCode ".toRegex()).toTypedArray()[1]
                    val versionCode = versionCodeAndFollowing.split("\n".toRegex()).toTypedArray()[0]
                    val newestVersionCode = versionCode.toInt()
                    val versionNameAndFollowing = versionCodeAndFollowing.split("\"".toRegex()).toTypedArray()[1]
                    val versionName = versionNameAndFollowing.split("\"".toRegex()).toTypedArray()[0]
                    editor.putString("newestVersion", versionName).apply()

                    // if newer update available, open update dialog
                    if (BuildConfig.VERSION_CODE != newestVersionCode) {
                        val updateDialog = UpdateDialog()
                        updateDialog.show(supportFragmentManager, javaClass.name)

                        // request permissions
                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES
                            ), 1
                        )
                    }
                }

                // if request is not succesful
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