package com.cyb3rko.logviewerforopenhab

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.aboutlibraries.LibsBuilder
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class About : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load save file
        val mySPR = getSharedPreferences("Safe", 0)

        // restore set orientation
        requestedOrientation = mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)

        // create and show about page
        val aboutPage = AboutPage(this)
            .setImage(R.mipmap.ic_launcher_foreground)
            .setDescription(getString(R.string.about_description)) // first item
            .addItem(
                Element().setTitle(String.format(getString(R.string.about_element_1), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                    .setIconDrawable(R.drawable.about_icon_github).setOnClickListener(showChangelog())
            ) // first group
            .addGroup(getString(R.string.about_group_1)) // second item
            .addItem(
                Element().setTitle(getString(R.string.about_element_2)).setIconDrawable(R.drawable._icon_libraries)
                    .setOnClickListener(showLibraries())
            ) // third item
            .addItem(
                Element().setTitle(getString(R.string.about_element_3)).setIconDrawable(R.drawable._icon_question).setOnClickListener(showIcons())
            ) // second group
            .addGroup(getString(R.string.about_group_2)) // feddback item
            .addItem(
                Element().setTitle(getString(R.string.about_element_feedback_text)).setIconDrawable(R.drawable.about_icon_github)
                    .setOnClickListener(openOnGithub())
            ) // email item
            .addEmail(getString(R.string.about_element_email_value), getString(R.string.about_element_email_text)) // website item
            .addWebsite(getString(R.string.about_element_website_value), getString(R.string.about_element_website_text)) // YouTube item
            .addItem(
                Element().setTitle(getString(R.string.about_element_youtube_text)).setIconDrawable(R.drawable.about_icon_youtube)
                    .setIconTint(R.color.about_youtube_color).setOnClickListener(openYouTubeProfile())
            ) // GitHub item
            .addGitHub(getString(R.string.about_element_github_value), getString(R.string.about_element_github_text)) // Instagram item
            .addItem(
                Element().setTitle(getString(R.string.about_element_instagram_text)).setIconDrawable(R.drawable.about_icon_instagram)
                    .setIconTint(R.color.about_instagram_color).setOnClickListener(openInstaPage())
            )
            .create()

        // set view (the about page)
        setContentView(aboutPage)
    }

    // show youtube profile
    private fun openYouTubeProfile(): View.OnClickListener? {
        return View.OnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_element_youtube_value)))) }
    }

    // show changelog
    private fun showChangelog(): View.OnClickListener? {
        return View.OnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_changelog_link)))) }
    }

    // get and show library credits on click
    private fun showLibraries(): View.OnClickListener? {
        return View.OnClickListener { // open library credits
            LibsBuilder()
                .withShowLoadingProgress(true)
                .withAboutVersionShownCode(false)
                .withAboutVersionShownName(false)
                .withAutoDetect(true)
                .withAboutIconShown(false)
                .withAboutVersionShown(false)
                .withVersionShown(true)
                .withLicenseDialog(true)
                .withLicenseShown(true)
                .withCheckCachedDetection(true)
                .withSortEnabled(true)
                .start(application)
        }
    }

    // show icon credits on click
    private fun showIcons(): View.OnClickListener? {
        return View.OnClickListener { startActivity(Intent(applicationContext, IconCredits::class.java)) }
    }

    // open project on GitHub on click
    private fun openOnGithub(): View.OnClickListener? {
        return View.OnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_element_feedback_value)))) }
    }

    // show instagram page
    private fun openInstaPage(): View.OnClickListener? {
        return View.OnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_element_instagram_value)))) }
    }
}