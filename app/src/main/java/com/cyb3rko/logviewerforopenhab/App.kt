package com.cyb3rko.logviewerforopenhab

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        val mySPR = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(mySPR.getString(NIGHTMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())!!.toInt())
    }
}