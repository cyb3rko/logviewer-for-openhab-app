package com.cyb3rko.logviewerforopenhab.appintro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cyb3rko.logviewerforopenhab.MainActivity
import com.cyb3rko.logviewerforopenhab.R
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
        addSlide(AppIntroFragment.newInstance(
            title = "Welcome...",
            description = "This is the unofficial LogViewer for openHAB app made by Cyb3rko",
            imageDrawable = R.drawable._icon_hello
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Feel free to contribute",
            imageDrawable = R.drawable._ic_github,
            description = "If you experience any problems or if you have a idea for a new feature, feel free to visit the GitHub repository and " +
                    "open an issue or a pull request.\nThanks!"
        ))
        addSlide(AppIntro3rdFragment.newInstance())
        addSlide(AppIntro4thFragment.newInstance())
        addSlide(AppIntro5thFragment.newInstance())
        addSlide(AppIntroFragment.newInstance(
            title = "Ready...",
            imageDrawable = R.drawable._ic_start,
            description = "Ok, everything is set up.\nEnjoy!"
        ))

        setTransformer(AppIntroPageTransformerType.Parallax())
        isWizardMode = true
        isSystemBackButtonLocked = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val mySPR = applicationContext.getSharedPreferences("Safe2", 0)
        val editor = mySPR.edit()

        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(mySPR.getBoolean("analyticsCollection", false))
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(mySPR.getBoolean("crashlyticsCollection", false))

        val date = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val sDF = SimpleDateFormat("yyyy-MM-dd")
        @SuppressLint("SimpleDateFormat") val sDF2 = SimpleDateFormat("HH:mm:ss")
        editor.putString("date", sDF.format(date))
        editor.putString("time", sDF2.format(date))
        editor.putBoolean("firstStart", false).apply()

        finish()
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}