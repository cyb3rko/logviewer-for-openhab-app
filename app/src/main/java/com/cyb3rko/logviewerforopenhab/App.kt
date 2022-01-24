package com.cyb3rko.logviewerforopenhab

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        val mySPR = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(mySPR.getString(NIGHTMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())!!.toInt())

        setWebViewDataDirectorySuffix()
    }

    private fun setWebViewDataDirectorySuffix() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName(this)
            if (processName != "com.cyb3rko.logviewerforopenhab" && processName != null) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    private fun getProcessName(context: Context?): String? {
        if (context == null) return null
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName
            }
        }
        return null
    }
}