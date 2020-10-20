package com.cyb3rko.logviewerforopenhab

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load save file
        val mySPR = getSharedPreferences("Safe", 0)
        // restore set orientation
        requestedOrientation = mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        // set view
        setContentView(R.layout.activity_privacy_policy)

        // set text of textviews
        val textViews = arrayOfNulls<TextView>(21)
        for (i in 1..21) {
            textViews[i - 1] = findViewById(resources.getIdentifier("textView$i", "id", packageName))
            textViews[i - 1]?.text = (resources.getStringArray(R.array.privacy_policy)[i - 1])
        }

        // floating action button clicklistener
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { // start intent for writing an e mail
            val mailIntent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse(
                String.format(
                    getString(R.string.mail_info), getString(R.string.app_name),
                    resources.getStringArray(R.array.privacy_policy)[0]
                )
            )
            mailIntent.data = data
            startActivity(Intent.createChooser(mailIntent, getString(R.string.send_mail)))
        }
    }
}