package com.cyb3rko.logviewerforopenhab.appintro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.cyb3rko.logviewerforopenhab.*
import com.cyb3rko.logviewerforopenhab.CONSENT_TIME
import com.cyb3rko.logviewerforopenhab.FIRST_START
import com.cyb3rko.logviewerforopenhab.SHARED_PREFERENCE
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.SimpleDateFormat
import java.util.*

class MyAppIntro : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, theme)
        addSlide(AppIntroFragment.newInstance(
            title = getString(R.string.intro_fragment1_title),
            description = getString(R.string.intro_fragment1_description),
            imageDrawable = R.drawable._ic_hello,
            backgroundColor = backgroundColor
        ))
        addSlide(AppIntroFragment.newInstance(
            title = getString(R.string.intro_fragment2_title),
            imageDrawable = R.drawable._ic_github,
            description = getString(R.string.intro_fragment2_description),
            backgroundColor = backgroundColor
        ))
        addSlide(AppIntro3rdFragment.newInstance())
        addSlide(AppIntro4thFragment.newInstance())
        addSlide(AppIntro5thFragment.newInstance())
        addSlide(AppIntroFragment.newInstance(
            title = getString(R.string.intro_fragment6_title),
            imageDrawable = R.drawable._ic_start,
            description = getString(R.string.intro_fragment6_description),
            backgroundColor = backgroundColor
        ))

        setTransformer(AppIntroPageTransformerType.Parallax())
        isWizardMode = true
        isSystemBackButtonLocked = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val mySPR = applicationContext.getSharedPreferences(SHARED_PREFERENCE, 0)
        val editor = mySPR.edit()

        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(mySPR.getBoolean(ANALYTICS_COLLECTION, true))
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(mySPR.getBoolean(CRASHLYTICS_COLLECTION, true))

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