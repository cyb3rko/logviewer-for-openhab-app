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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val githubIcon = mehdi.sakout.aboutpage.R.drawable.about_icon_github
        val emailIcon = mehdi.sakout.aboutpage.R.drawable.about_icon_email
        val instagramIcon = mehdi.sakout.aboutpage.R.drawable.about_icon_instagram
        val instagramColor = mehdi.sakout.aboutpage.R.color.about_instagram_color

        return AboutPage(context)
            .setImage(R.mipmap.ic_launcher_foreground)
            .setDescription(getString(R.string.about_description))
            .addItem(
                Element().setTitle(String.format(getString(R.string.about_element_version), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                    .setIconDrawable(githubIcon).setOnClickListener(showChangelog())
            )
            .addGroup(getString(R.string.about_group_legal))
            .addItem(
                Element().setTitle(getString(R.string.about_element_libraries)).setIconDrawable(R.drawable._ic_libraries)
                    .setOnClickListener(showLibraries())
            )
            .addItem(
                Element().setTitle(getString(R.string.about_element_icons)).setIconDrawable(R.drawable._ic_question).setOnClickListener(showIcons())
            )
            .addItem(
                Element().setTitle(getString(R.string.about_element_animations)).setIconDrawable(R.drawable._ic_question)
                    .setOnClickListener(showAnimations())
            )
            .addGroup(getString(R.string.about_group_connect))
            .addItem(
                Element().setTitle(getString(R.string.about_element_feedback_text)).setIconDrawable(githubIcon)
                    .setOnClickListener(openGithubFeedback())
            )
            .addItem(
                Element().setTitle(getString(R.string.about_element_email_text)).setIconDrawable(emailIcon)
                    .setOnClickListener(writeEmail())
            )
            .addItem(
                Element().setTitle(getString(R.string.about_element_github_text))
                    .setIconDrawable(githubIcon).setOnClickListener(openGithubProfile())
            )
            .addItem(
                Element().setTitle(getString(R.string.about_element_instagram_text)).setIconDrawable(instagramIcon)
                    .setIconTint(instagramColor).setOnClickListener(openInstaPage())
            )
            .create()
    }

    private fun showChangelog(): View.OnClickListener {
        return View.OnClickListener { openUrl(getString(R.string.about_changelog_link)) }
    }

    private fun showLibraries(): View.OnClickListener {
        return View.OnClickListener {
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
    }

    private fun showIcons(): View.OnClickListener {
        return View.OnClickListener { findNavController().navigate(R.id.nav_about_icons) }
    }

    private fun showAnimations(): View.OnClickListener {
        return View.OnClickListener { findNavController().navigate(R.id.nav_about_animations) }
    }

    private fun openGithubFeedback(): View.OnClickListener {
        return View.OnClickListener { openUrl("https://github.com/cyb3rko/logviewer-for-openhab-app/") }
    }

    private fun openGithubProfile(): View.OnClickListener {
        return View.OnClickListener { openUrl("https://github.com/cyb3rko/") }
    }

    private fun openInstaPage(): View.OnClickListener {
        return View.OnClickListener { openUrl("https://instagram.com/_u/cyb3rko") }
    }

    private fun writeEmail(): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent().apply {
                this.action = Intent.ACTION_SENDTO
                this.type = "text/plain"
                this.data = Uri.parse("mailto:")
                this.putExtra(Intent.EXTRA_EMAIL, arrayOf("niko@cyb3rko.de"))
            }
            startActivity(intent)
        }
    }
}