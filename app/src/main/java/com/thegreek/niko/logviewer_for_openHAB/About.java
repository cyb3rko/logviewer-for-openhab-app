package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.mikepenz.aboutlibraries.LibsBuilder;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load save file
        SharedPreferences mySPR = this.getSharedPreferences("Safe", 0);

        // restore set orientation
        setRequestedOrientation(mySPR.getInt("orientation", 0));

        // create and show about page
        View aboutPage = new AboutPage(this)
                .setImage(R.mipmap.ic_launcher_foreground)
                .setDescription(getString(R.string.about_description))
                // first item
                .addItem(new Element().setTitle(String.format(getString(R.string.about_element_1), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)).setIconDrawable(R.drawable.about_icon_github).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_changelog_link))));
                    }
                }))
                // first group
                .addGroup(getString(R.string.about_group_1))
                // second item
                .addItem(new Element().setTitle(getString(R.string.about_element_2)).setIconDrawable(R.drawable.icon_libraries).setOnClickListener(showLibraries()))
                // third item
                .addItem(new Element().setTitle(getString(R.string.about_element_3)).setIconDrawable(R.drawable.icon_question).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // open icon credits
                        Intent termsOfUseIntent = new Intent(getApplicationContext(), IconCredits.class);
                        startActivity(termsOfUseIntent);
                    }
                }))
                // second group
                .addGroup(getString(R.string.about_group_2))
                // email item
                .addEmail(getString(R.string.about_element_email_value), getString(R.string.about_element_email_text))
                // website item
                .addWebsite(getString(R.string.about_element_website_value), getString(R.string.about_element_website_text))
                // YouTube item
                .addYoutube(getString(R.string.about_element_youtube_value), getString(R.string.about_element_youtube_text))
                // GitHub item
                .addGitHub(getString(R.string.about_element_github_value), getString(R.string.about_element_github_text))
                // Instagram item
                .addInstagram(getString(R.string.about_element_instagram_value), getString(R.string.about_element_instagram_text))
                .create();

        // set view (the about page)
        setContentView(aboutPage);
    }

    // get and show library credits on click
    private View.OnClickListener showLibraries() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open library credits
                new LibsBuilder()
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
                        .start(getApplication());
            }
        };
    }
}