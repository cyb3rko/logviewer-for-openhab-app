package com.thegreek.niko.logviewer_for_openHAB;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class WebViewFragment extends Fragment {
    private FloatingActionButton backButton;
    private FloatingActionButton textButton;
    private FloatingActionButton viewButton;
    private SharedPreferences mySPR;
    private SharedPreferences.Editor editor;
    private String textSizeType;
    private WebSettings webSettings;
    private WebView webView;

    public static boolean connected;
    private boolean viewLocked = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = v.findViewById(R.id.webview);
        viewButton = v.findViewById(R.id.view_button);
        textButton = v.findViewById(R.id.text_button);
        backButton = v.findViewById(R.id.back_button);

        // load save file and its editor
        mySPR = v.getContext().getSharedPreferences("Safe", 0);
        editor = mySPR.edit();
        editor.apply();

        // adapt settings for webview
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        v.setVerticalScrollBarEnabled(false);
        setTouchable(false);

        // restore text size (according to orientation)
        if (getResources().getConfiguration().orientation == 1) {
            textSizeType = "textSizePortrait";
        } else {
            textSizeType = "textSizeOther";
        }

        webSettings.setTextZoom(mySPR.getInt(textSizeType, 60));

        // open link
        webView.loadUrl(mySPR.getString("link", ""));

        // permanent scroll
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (viewLocked) {
                    webView.scrollBy(0, 10000);
                }

                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 500);

        setViewButtonClickListener();
        setTextButtonClickListener(v);
        setBackButtonClickListener();

        showTapTargetSequence(v);

        return v;
    }

    // onClickListener for viwe button
    private void setViewButtonClickListener() {
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if scrolling locked
                if (viewLocked) {
                    viewButton.setImageResource(R.drawable.icon_lock_2);
                    viewLocked = false;
                    setTouchable(true);

                    // show toast
                    Toasty.info(view.getContext(), getString(R.string.lock_button_1), Toasty.LENGTH_SHORT).show();
                } else {
                    viewButton.setImageResource(R.drawable.icon_lock);
                    viewLocked = true;
                    setTouchable(false);

                    //scroll to bottom
                    webView.scrollBy(0, 10000);

                    // show toast
                    Toasty.info(view.getContext(), getString(R.string.lock_button_2), Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    // dialog to change text size
    private void setTextButtonClickListener(final View v) {
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // create dialog builder
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                // current text size
                final int currentTextSize = mySPR.getInt(textSizeType, 60);
                // add textview for showing text size
                final TextView sizeView = new TextView(v.getContext());
                // add seekbar for choosing text size
                final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(v.getContext());

                // create title
                TextView titleView = new TextView(v.getContext());
                titleView.setText(R.string.text_size_dialog_title);
                titleView.setTextSize(22);
                titleView.setTypeface(Typeface.DEFAULT_BOLD);
                titleView.setPadding(32, 32, 32, 32);
                titleView.setGravity(Gravity.CENTER_HORIZONTAL);

                LinearLayout content = new LinearLayout(v.getContext());
                content.setOrientation(LinearLayout.VERTICAL);

                // set size view
                sizeView.setText(String.format(getString(R.string.text_size_dialog_text), currentTextSize, currentTextSize));
                sizeView.setTextSize(18);
                sizeView.setPadding(24, 24, 24, 50);
                sizeView.setGravity(Gravity.CENTER_HORIZONTAL);
                content.addView(sizeView);

                // set seekbar
                discreteSeekBar.setMax(100);
                discreteSeekBar.setMin(1);
                discreteSeekBar.setProgress(webSettings.getTextZoom());
                discreteSeekBar.setPadding(50,0,50,50);
                discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                    @Override
                    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                        // nothing to clean up (for PMD)
                    }

                    @Override
                    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                        // nothing to clean up (for PMD)
                    }

                    // if user stops touching seekbar
                    @Override
                    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                        sizeView.setText(String.format(getString(R.string.text_size_dialog_text), currentTextSize, discreteSeekBar.getProgress()));
                    }
                });
                content.addView(discreteSeekBar);

                // create dialog
                builder.setCustomTitle(titleView)
                        .setView(content)
                        // add right button
                        .setPositiveButton(getString(R.string.text_size_dialog_button_1), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int textSize = discreteSeekBar.getProgress();

                                // change text size
                                webSettings.setTextZoom(textSize);
                                // store new size
                                editor.putInt(textSizeType, textSize).apply();
                                // show toast
                                Toasty.success(view.getContext(), getString(R.string.text_size_changed) + textSize, Toasty.LENGTH_SHORT).show();
                            }
                        })
                        // add left button
                        .setNegativeButton(getString(R.string.text_size_dialog_button_2), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing to clean up (for PMD)
                            }
                        })
                        // show dialog
                        .create().show();
            }
        });
    }

    // onClickListener for back button
    private void setBackButtonClickListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset autoStart temporarily to be able to switch to menu without directly reconnecting
                final boolean autoStartTemp = mySPR.getBoolean("autoStart", false);
                editor.putBoolean("autoStart", false).apply();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.start, new MainFragment()).commit();

                final Handler handler2 = new Handler();
                Runnable runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        editor.putBoolean("autoStart", autoStartTemp).apply();
                    }
                };

                handler2.postDelayed(runnable2, 1);
                editor.putBoolean("connected", false).apply();
            }
        });
    }

    // taptargetsequence to explain buttons
    private void showTapTargetSequence(View v) {
        // if first log visit
        if (mySPR.getBoolean("firstStartWeb", true)) {
            new TapTargetSequence(Objects.requireNonNull(getActivity()))
                    .targets(
                            // first target (view button)
                            TapTarget.forView(v.findViewById(R.id.view_button), getString(R.string.tap_target_title_1), getString(R.string.tap_target_message_1))
                                    // many settings
                                    .transparentTarget(true)
                                    .tintTarget(false)
                                    .outerCircleColor(R.color.colorAccent)
                                    .tintTarget(false)
                                    .titleTextSize(18)
                                    .descriptionTextSize(16)
                                    .drawShadow(true)
                                    .cancelable(false),
                            // first target (text button)
                            TapTarget.forView(v.findViewById(R.id.text_button), getString(R.string.tap_target_title_2), getString(R.string.tap_target_message_2))
                                    // many settings
                                    .transparentTarget(true)
                                    .tintTarget(false)
                                    .outerCircleColor(R.color.colorAccent)
                                    .tintTarget(false)
                                    .titleTextSize(18)
                                    .descriptionTextSize(16)
                                    .drawShadow(true)
                                    .cancelable(false),
                            // first target (back button)
                            TapTarget.forView(v.findViewById(R.id.back_button), getString(R.string.tap_target_title_3), getString(R.string.tap_target_message_3))
                                    // many settings
                                    .transparentTarget(true)
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

                        // on every single new target
                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            // nothing to clean up (for POM)
                        }

                        // if sequence has been canceled
                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            // nothing to clean up (for POM)
                        }
                    })
                    // start sequence
                    .start();
        }
    }

    // switch touchability
    @SuppressLint("ClickableViewAccessibility")
    private void setTouchable(boolean b) {
        if (b) {
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
        } else {
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
    }
}
