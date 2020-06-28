package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mikepenz.aboutlibraries.LibsBuilder;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_foreground)
                .setDescription(getString(R.string.about_description))
                .addItem(new Element().setTitle("Version " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")").setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nikothegreek/logviewer-for-openhab-app/releases")));
                    }
                }))
                .addGroup("Legal")
                .addItem(new Element().setTitle("Used Libraries").setIconDrawable(R.drawable.icon_libraries).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                }))
                .addItem(new Element().setTitle("Used Icons").setIconDrawable(R.drawable.icon_question).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent termsOfUseIntent = new Intent(getApplicationContext(), IconCredits.class);
                        startActivity(termsOfUseIntent);
                    }
                }))
                .addGroup("Connect with me")
                .addEmail("nikodiamond3@gmail.com", "Contact me")
                .addWebsite("https://nikothegreek.jimdofree.com", "Visit my website (currently only in German!)")
                .addYoutube("UCue_SZXdF8yZByavetBU1ZQ", "Watch my tutorial videos")
                .addGitHub("nikothegreek", "Take a look at my other projects")
                .addInstagram("cyb3rko", "Follow me")
                .create();

        setContentView(aboutPage);
    }
}