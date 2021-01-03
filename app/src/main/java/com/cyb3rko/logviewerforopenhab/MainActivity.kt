package com.cyb3rko.logviewerforopenhab

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.cyb3rko.logviewerforopenhab.appintro.MyAppIntro
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mySPR: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toasty.Config.getInstance().allowQueue(false).apply()
        mySPR = getSharedPreferences(SHARED_PREFERENCE, 0)
        editor = mySPR.edit()
        editor.apply()
        requestedOrientation = mySPR.getString(ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED.toString())?.toInt()!!

        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(R.navigation.mobile_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_menu), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)

        if (mySPR.getBoolean(FIRST_START, true) || mySPR.getString(CONSENT_DATE, "") == "") {
            finish()
            startActivity(Intent(applicationContext, MyAppIntro::class.java))
        } else {
            if (mySPR.getBoolean(AUTO_START, false)) navController.navigate(R.id.nav_webview)
            if (mySPR.getBoolean(AUTO_UPDATE, false)) updateCheck(this)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        navView.setNavigationItemSelectedListener {
            toolbar.visibility = View.VISIBLE
            when (it.itemId) {
                R.id.nav_menu -> navController.navigate(R.id.nav_menu)
                R.id.nav_settings -> navController.navigate(R.id.nav_settings)
                R.id.drawer_about -> navController.navigate(R.id.nav_about)
                R.id.drawer_end_user_consent -> {
                    var dialogMessage = getString(R.string.end_user_consent_2_message_1)
                    dialogMessage += mySPR.getString(CONSENT_DATE, getString(R.string.end_user_consent_2_date_not_found)) +
                            getString(R.string.end_user_consent_2_message_2) +
                            mySPR.getString(CONSENT_TIME, getString(R.string.end_user_consent_2_time_not_found))
                    val spannableString = SpannableString(dialogMessage)
                    val drawerMenu = navView.menu
                    val clickableSpan1 = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            showLicenseDialog(this@MainActivity, PRIVACY_POLICY)
                        }
                    }
                    val clickableSpan2 = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            showLicenseDialog(this@MainActivity, TERMS_OF_USE)
                        }
                    }
                    var currentText = getString(R.string.end_user_consent_2_privacy_policy)
                    var index = dialogMessage.indexOf(currentText)
                    spannableString.setSpan(
                        clickableSpan1, index, index + currentText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    currentText = getString(R.string.end_user_consent_2_terms_of_use)
                    index = dialogMessage.indexOf(currentText)
                    spannableString.setSpan(
                        clickableSpan2, index, index + currentText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    currentText = getString(R.string.end_user_consent_2_date)
                    index = dialogMessage.indexOf(currentText)
                    for (i in 0..1) {
                        spannableString.setSpan(UnderlineSpan(), index, index + currentText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        currentText = getString(R.string.end_user_consent_2_date)
                        index = dialogMessage.indexOf(currentText)
                    }

                    MaterialDialog(this).show {
                        title(R.string.end_user_consent_2_title)
                        message(0, spannableString) {
                            messageTextView.movementMethod = LinkMovementMethod.getInstance()
                        }
                        positiveButton(android.R.string.ok) {
                            drawerMenu.findItem(R.id.drawer_end_user_consent).isChecked = false
                        }
                        negativeButton(R.string.end_user_consent_2_button2) {
                            val analytics = FirebaseAnalytics.getInstance(applicationContext)
                            analytics.resetAnalyticsData()
                            analytics.setAnalyticsCollectionEnabled(false)
                            val crashlytics = FirebaseCrashlytics.getInstance()
                            crashlytics.deleteUnsentReports()
                            crashlytics.setCrashlyticsCollectionEnabled(false)
                            editor.clear().commit()
                            finish()
                            startActivity(Intent(applicationContext, this@MainActivity::class.java))
                        }
                        onCancel {
                            drawerMenu.findItem(R.id.drawer_end_user_consent).isChecked = false
                        }
                    }
                }
            }
            it.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        if (mySPR.getBoolean(CONNECTION_OVERVIEW_ENABLED, true)) { restoreConnections() }
    }

    private fun restoreConnections() {
        showConnections(mySPR, getListOfConnections(mySPR), this)
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
                    editor.putString(NEWEST_VERSION, versionName).apply()

                    if (BuildConfig.VERSION_CODE < newestVersionCode) {
                        Log.d(this@MainActivity::class.java.simpleName, "Update available: $versionName")
                        val dialogMessage = String.format(getString(R.string.update_dialog_message), "$versionName ($versionCode)",
                            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                        val changelog = "changelog"
                        val index = dialogMessage.indexOf(changelog)
                        val spannableString = SpannableString(dialogMessage)
                        spannableString.setSpan(object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.update_changelog_link))))
                            }
                        }, index, index + changelog.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                        MaterialDialog(this@MainActivity).show {
                            title(R.string.update_dialog_title)
                            message(0, spannableString) {
                                messageTextView.movementMethod = LinkMovementMethod.getInstance()
                            }
                            positiveButton(R.string.update_dialog_button_1) {
                                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                    downloadNewestApk(applicationContext, mySPR.getString(NEWEST_VERSION, "")!!)
                                } else {
                                    Toasty.error(context, getString(R.string.update_dialog_error), Toasty.LENGTH_LONG).show()
                                }
                            }
                        }

                        ActivityCompat.requestPermissions(activity, arrayOf(
                            Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 1)
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e(this@MainActivity::class.java.simpleName, String.format(getString(R.string.update_dialog_failed, anError)))
                }
            })
    }

    private fun downloadNewestApk(context: Context, version: String) {
        val link = String.format(context.getString(R.string.update_download_link), version)

        val request = DownloadManager.Request(Uri.parse(link)).setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(link, null, null)
        ).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    override fun onBackPressed() {
        if (drawerLayout.isOpen) drawerLayout.close() else super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}