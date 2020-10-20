package com.cyb3rko.logviewerforopenhab

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyb3rko.abouticons.AboutIcons

class IconCredits : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load save file
        val mySPR = getSharedPreferences("Safe", 0)

        // restore set orientation
        requestedOrientation = mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)

        // set view
        setContentView(AboutIcons(this, R.drawable::class.java).get())
    }
}