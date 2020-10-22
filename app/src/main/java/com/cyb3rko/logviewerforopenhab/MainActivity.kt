package com.cyb3rko.logviewerforopenhab

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mySPR: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mySPR = getSharedPreferences("Safe", 0)
        editor = mySPR.edit()
        editor.apply()
        requestedOrientation = mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(if (mySPR.getBoolean("autoStart", false)) R.navigation.mobile_navigation2 else R.navigation.mobile_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_menu), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.close, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Toasty.Config.getInstance().allowQueue(false).apply()

        mySPR = getSharedPreferences("Safe", 0)
        editor = mySPR.edit()
        editor.apply()

        if (mySPR.getBoolean("firstStart", true) || mySPR.getString("date", "") == "") {
//            val endUserConsent = EndUserConsent(true)
//            endUserConsent.isCancelable = false
//            endUserConsent.show(supportFragmentManager, javaClass.name)

        } else {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)

            if (!mySPR.getBoolean("tempDisableStart", false)) {
//                updateCheck(this)
            }
        }

        navView.setNavigationItemSelectedListener {
            toolbar.visibility = View.VISIBLE
            when (it.itemId) {
                R.id.nav_menu -> navController.navigate(R.id.nav_menu)
//                R.id.nav_settings -> navController.navigate(R.id.open_about)
                R.id.drawer_about -> navController.navigate(R.id.nav_about)
                R.id.drawer_end_user_consent -> {
                    MaterialDialog(this).show {
                        title(R.string.end_user_consent_2_title)
                        var dialogMessage = getString(R.string.end_user_consent_2_message_1)
                        dialogMessage += mySPR.getString("date", "") + getString(R.string.end_user_consent_2_message_2) + mySPR.getString("time", "")
                        message(0, dialogMessage)
                        positiveButton(R.string.end_user_consent_2_button)
                    }
                }
                R.id.drawer_privacy_policy -> navController.navigate(R.id.nav_privacy_policy)
                R.id.drawer_terms_of_use -> navController.navigate(R.id.nav_terms_of_use)
            }
            it.isChecked = true
            drawerLayout.closeDrawers()
            true
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

                        ActivityCompat.requestPermissions(activity, arrayOf(
                            Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
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

    override fun onBackPressed() {
        if (drawerLayout.isOpen) {
            drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}