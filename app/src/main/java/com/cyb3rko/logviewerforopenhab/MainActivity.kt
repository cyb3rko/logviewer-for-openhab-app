package com.cyb3rko.logviewerforopenhab

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cyb3rko.logviewerforopenhab.appintro.MyAppIntro
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mySPR: SharedPreferences
    private lateinit var navController: NavController
    lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mySPR = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
        editor = mySPR.edit()
        editor.apply()

        requestedOrientation = mySPR.getString(
            ORIENTATION,
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED.toString()
        )?.toInt()!!

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
        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)

        if (mySPR.getBoolean(FIRST_START, true) || mySPR.getString(CONSENT_DATE, "") == "") {
            finish()
            startActivity(Intent(applicationContext, MyAppIntro::class.java))
        } else if (mySPR.getBoolean(AUTO_START, false)) {
            navController.navigate(R.id.nav_webview)
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
                    showEndUserConsent()
                }
                R.id.drawer_github_repo -> {
                    openUrl("https://" + getString(R.string.github_repo))
                }
                R.id.drawer_translation -> {
                    openUrl("https://" + getString(R.string.translation_project))
                }
            }
            it.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        if (mySPR.getBoolean(CONNECTION_OVERVIEW_ENABLED, true)) { restoreConnections() }
        requestReview()
    }

    private fun restoreConnections() {
        try {
            showConnections(mySPR, getListOfConnections(mySPR), this)
        } catch (e: Exception) {
            editor.putString(CONNECTIONS, "empty").apply()
        }
    }

    private fun showEndUserConsent() {
        var dialogMessage = getString(R.string.end_user_consent_2_message_1)
        dialogMessage +=
            mySPR.getString(CONSENT_DATE, getString(R.string.end_user_consent_2_date_not_found)) +
                    getString(R.string.end_user_consent_2_message_2) +
                    mySPR.getString(CONSENT_TIME, getString(R.string.end_user_consent_2_time_not_found))
        val spannableString = SpannableString(dialogMessage)
        var currentText = getString(R.string.end_user_consent_2_date)
        var index = dialogMessage.indexOf(currentText)
        repeat(2) {
            spannableString.setSpan(
                UnderlineSpan(), index, index + currentText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            currentText = getString(R.string.end_user_consent_2_time)
            index = dialogMessage.indexOf(currentText)
        }

        val drawerMenu = navView.menu

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.end_user_consent_2_title)
            .setMessage(spannableString)
            .setView(R.layout.dialog_end_user_consent)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                drawerMenu.findItem(R.id.drawer_end_user_consent).isChecked = false
            }
            .setNegativeButton(R.string.end_user_consent_2_button_2) { _, _ ->
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
            .setOnCancelListener {
                drawerMenu.findItem(R.id.drawer_end_user_consent).isChecked = false
            }
            .create().apply {
                setOnShowListener {
                    window?.findViewById<MaterialButton>(R.id.privacy_policy_button)?.setOnClickListener {
                        showLicenseDialog(this@MainActivity, PRIVACY_POLICY)
                    }
                    window?.findViewById<MaterialButton>(R.id.terms_of_use_button)?.setOnClickListener {
                        showLicenseDialog(this@MainActivity, TERMS_OF_USE)
                    }
                }
            }
            .show()
    }

    private fun requestReview() {
        val revision = mySPR.getInt(REVIEW_REVISION, 0)

        if (revision <= 1) {
            val counter = mySPR.getInt(REVIEW_COUNTER, 0) + 1
            editor.putInt(REVIEW_COUNTER, counter).apply()

            when (revision) {
                0 -> {
                    if (counter >= 5) {
                        showReviewDialog(0)
                    }
                }
                1 -> {
                    if (counter >= 10) {
                        showReviewDialog(1)
                    }
                }
            }
        }
    }

    private fun showReviewDialog(revision: Int) {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener {
            if (it.isSuccessful) {
                manager.launchReviewFlow(this, it.result).apply {
                    addOnCompleteListener {
                        editor.putInt(REVIEW_REVISION, revision + 1).apply()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        setToolbarVisibility(this, View.VISIBLE)
        if (drawerLayout.isOpen) drawerLayout.close() else super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
