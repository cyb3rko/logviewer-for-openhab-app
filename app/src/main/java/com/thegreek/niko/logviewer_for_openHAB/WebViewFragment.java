package com.thegreek.niko.logviewer_for_openHAB;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import es.dmoral.toasty.Toasty;


public class WebViewFragment extends Fragment {
    private boolean viewLocked = true;

    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_web_view, container, false);

        final WebView webView = v.findViewById(R.id.webView);
        final FloatingActionButton viewButton = v.findViewById(R.id.viewButton);
        final FloatingActionButton textButton = v.findViewById(R.id.textButton);
        final FloatingActionButton backButton = v.findViewById(R.id.backButton);

        final SharedPreferences mySPR = this.getActivity().getSharedPreferences("Speicherstand", 0);
        editor = mySPR.edit();

        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setTextZoom(60);
        v.setVerticalScrollBarEnabled(false);

        webView.loadUrl(mySPR.getString("link", ""));

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (viewLocked) {
                    webView.scrollBy(0, 10000);
                }

                handler.postDelayed(this, 60);
            }
        };

        handler.postDelayed(runnable, 500);

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewLocked) {
                    viewButton.setImageResource(R.drawable.icon_view);
                    viewLocked = false;

                    Toasty.info(view.getContext(), "Auto-Scroll deactivated", Toasty.LENGTH_SHORT).show();
                } else {
                    viewButton.setImageResource(R.drawable.icon_lock);
                    viewLocked = true;

                    Toasty.info(view.getContext(), "Auto-Scroll activated", Toasty.LENGTH_SHORT).show();
                }
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(v.getContext());

                TextView titleView = new TextView(getContext());
                titleView.setText(R.string.text_size_alert);
                titleView.setTextSize(22);
                titleView.setTypeface(Typeface.DEFAULT_BOLD);
                titleView.setPadding(32, 32, 32, 32);
                titleView.setGravity(Gravity.CENTER_HORIZONTAL);

                discreteSeekBar.setMax(100);
                discreteSeekBar.setMin(1);
                discreteSeekBar.setProgress(webSettings.getTextZoom());
                discreteSeekBar.setPadding(50,80,50,50);

                builder.setView(discreteSeekBar)
                        .setCustomTitle(titleView)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int textSize = discreteSeekBar.getProgress();

                            webSettings.setTextZoom(textSize);
                            editor.putInt("textSize", textSize).apply();
                            Toasty.success(view.getContext(), "Text size changed to " + textSize, Toasty.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create().show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean autoStartTemp = mySPR.getBoolean("autoStart", false);
                editor.putBoolean("autoStart", false).apply();
                getFragmentManager().beginTransaction().replace(R.id.start, new MainFragment()).commit();

                final Handler handler2 = new Handler();
                Runnable runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        editor.putBoolean("autoStart", autoStartTemp).apply();
                    }
                };

                handler2.postDelayed(runnable2, 1);
            }
        });

        if (mySPR.getBoolean("firstStartWeb", true)) {
            new TapTargetSequence(getActivity())
                    .targets(
                            TapTarget.forView(v.findViewById(R.id.viewButton), "Auto-Scroll", "Click here to switch the Auto-Scroll mode")
                                    .tintTarget(false)
                                    .outerCircleColor(R.color.colorAccent)
                                    .tintTarget(false)
                                    .titleTextSize(18)
                                    .descriptionTextSize(16)
                                    .drawShadow(true)
                                    .cancelable(false),
                            TapTarget.forView(v.findViewById(R.id.textButton), "Text size", "Click here to change the text size")
                                    .tintTarget(false)
                                    .outerCircleColor(R.color.colorAccent)
                                    .tintTarget(false)
                                    .titleTextSize(18)
                                    .descriptionTextSize(16)
                                    .drawShadow(true)
                                    .cancelable(false),
                            TapTarget.forView(v.findViewById(R.id.backButton), "Go back", "Click here to go back to the menu")
                                    .tintTarget(false)
                                    .outerCircleColor(R.color.colorAccent)
                                    .tintTarget(false)
                                    .titleTextSize(18)
                                    .descriptionTextSize(16)
                                    .drawShadow(true)
                                    .cancelable(false)
                    ).listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {
                    editor.putBoolean("firstStartWeb", false).apply();
                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                }

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {
                }
            }).start();
        }

        return v;
    }
}
