package com.cyb3rko.logviewerforopenhab.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cyb3rko.logviewerforopenhab.BuildConfig
import com.cyb3rko.logviewerforopenhab.R
import com.cyb3rko.logviewerforopenhab.openUrl
import com.mikepenz.aboutlibraries.LibsBuilder
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val githubIcon = mehdi.sakout.aboutpage.R.drawable.about_icon_github
        val emailIcon = mehdi.sakout.aboutpage.R.drawable.about_icon_email
        val instagramIcon = mehdi.sakout.aboutpage.R.drawable.about_icon_instagram
        val instagramColor = mehdi.sakout.aboutpage.R.color.about_instagram_color

        return AboutPage(context)
            .setImage(R.mipmap.ic_launcher_foreground)
            .setDescription(getString(R.string.about_description))
            .addItem(
                Element(
                    String.format(
                        getString(R.string.about_element_version),
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                    githubIcon
                ).setOnClickListener { openUrl(getString(R.string.about_changelog_link)) }
            )
            .addGroup(getString(R.string.about_group_legal))
            .addItem(
                Element(
                    getString(R.string.about_element_libraries),
                    R.drawable._ic_libraries
                ).setOnClickListener { showLibraries() }
            )
            .addItem(
                Element(
                    getString(R.string.about_element_icons),
                    R.drawable._ic_question
                ).setOnClickListener { findNavController().navigate(R.id.nav_about_icons) }
            )
            .addItem(
                Element(
                    getString(R.string.about_element_animations),
                    R.drawable._ic_question
                ).setOnClickListener { findNavController().navigate(R.id.nav_about_animations) }
            )
            .addGroup(getString(R.string.about_group_connect))
            .addItem(
                Element(
                    getString(R.string.about_element_feedback_text),
                    githubIcon
                ).setOnClickListener { openUrl("https://github.com/cyb3rko/logviewer-for-openhab-app/") }
            )
            .addItem(
                Element(
                    getString(R.string.about_element_email_text),
                    emailIcon
                ).setOnClickListener { writeEmail() }
            )
            .addItem(
                Element(
                    getString(R.string.about_element_github_text),
                    githubIcon
                ).setOnClickListener { openUrl("https://github.com/cyb3rko/") }
            )
            .addItem(
                Element(
                    getString(R.string.about_element_instagram_text),
                    instagramIcon
                )
                    .setIconTint(instagramColor)
                    .setOnClickListener { openUrl("https://instagram.com/_u/cyb3rko") }
            )
            .create()
    }

    private fun showLibraries() {
        context?.let { validContext ->
            LibsBuilder()
                .withLicenseShown(true)
                .withAboutIconShown(false)
                .withAboutVersionShown(false)
                .withActivityTitle(getString(R.string.about_element_libraries))
                .withSearchEnabled(true)
                .start(validContext)
        }
    }

    private fun writeEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            setDataAndType(Uri.parse("mailto:"), "text/plain")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("niko@cyb3rko.de"))
        }
        startActivity(intent)
    }
}
