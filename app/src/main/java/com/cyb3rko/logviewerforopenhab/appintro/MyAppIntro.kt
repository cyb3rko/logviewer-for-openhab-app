package com.cyb3rko.logviewerforopenhab.appintro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cyb3rko.logviewerforopenhab.*
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.SimpleDateFormat
import java.util.*

class MyAppIntro : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.intro_fragment1_title),
            description = getString(R.string.intro_fragment1_description),
            imageDrawable = R.drawable._ic_hello,
            backgroundColorRes = R.color.colorPrimary
        ))
        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.intro_fragment2_title),
            imageDrawable = R.drawable._ic_github,
            description = getString(R.string.intro_fragment2_description),
            backgroundColorRes = R.color.colorPrimary
        ))
        addSlide(AppIntro3rdFragment.newInstance())
        addSlide(AppIntro4thFragment.newInstance())
        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.intro_fragment5_title),
            imageDrawable = R.drawable._ic_start,
            description = getString(R.string.intro_fragment5_description),
            backgroundColorRes = R.color.colorPrimary
        ))

        showStatusBar(true)
        isWizardMode = true
        isSystemBackButtonLocked = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val mySPR = applicationContext.getSharedPreferences(SHARED_PREFERENCE, 0)
        val editor = mySPR.edit()

        FirebaseAnalytics.getInstance(this)
            .setAnalyticsCollectionEnabled(mySPR.getBoolean(ANALYTICS_COLLECTION, true))
        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(mySPR.getBoolean(CRASHLYTICS_COLLECTION, true))

        val date = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val sDF = SimpleDateFormat("yyyy-MM-dd")
        @SuppressLint("SimpleDateFormat") val sDF2 = SimpleDateFormat("HH:mm:ss")
        editor.putString(CONSENT_DATE, sDF.format(date))
        editor.putString(CONSENT_TIME, sDF2.format(date))
        editor.putBoolean(FIRST_START, false).apply()

        finish()
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}
