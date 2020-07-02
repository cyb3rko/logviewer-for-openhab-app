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

    private SharedPreferences.Editor editor;
    private WebView webView;

    private boolean viewLocked = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = v.findViewById(R.id.webview);
        final FloatingActionButton viewButton = v.findViewById(R.id.view_button);
        final FloatingActionButton textButton = v.findViewById(R.id.text_button);
        final FloatingActionButton backButton = v.findViewById(R.id.back_utton);
        final String textSizeType;

        final SharedPreferences mySPR = Objects.requireNonNull(this.getActivity()).getSharedPreferences("Safe", 0);
        editor = mySPR.edit();

        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (getResources().getConfiguration().orientation == 1) {
            textSizeType = "textSizePortrait";
        } else {
            textSizeType = "textSizeOther";
        }

        webSettings.setTextZoom(mySPR.getInt(textSizeType, 60));
        v.setVerticalScrollBarEnabled(false);
        setTouchable(false);

        webView.loadUrl(mySPR.getString("link", ""));

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

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewLocked) {
                    viewButton.setImageResource(R.drawable.icon_lock_2);
                    viewLocked = false;
                    setTouchable(true);

                    Toasty.info(view.getContext(), getString(R.string.lock_button_1), Toasty.LENGTH_SHORT).show();
                } else {
                    viewButton.setImageResource(R.drawable.icon_lock);
                    viewLocked = true;
                    setTouchable(false);
                    webView.scrollBy(0, 10000);

                    Toasty.info(view.getContext(), getString(R.string.lock_button_2), Toasty.LENGTH_SHORT).show();
                }
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(v.getContext());

                TextView titleView = new TextView(getContext());
                titleView.setText(R.string.text_size_dialog_title);
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
                        .setPositiveButton(getString(R.string.text_size_dialog_button_1), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int textSize = discreteSeekBar.getProgress();

                                webSettings.setTextZoom(textSize);
                                editor.putInt(textSizeType, textSize).apply();
                                Toasty.success(view.getContext(), getString(R.string.text_size_changed) + textSize, Toasty.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(getString(R.string.text_size_dialog_button_2), new DialogInterface.OnClickListener() {
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
            }
        });

        if (mySPR.getBoolean("firstStartWeb", true)) {
            new TapTargetSequence(getActivity())
                    .targets(
                            TapTarget.forView(v.findViewById(R.id.view_button), getString(R.string.tap_target_title_1), getString(R.string.tap_target_message_1))
                                    .transparentTarget(true)
                                    .tintTarget(false)
                                    .outerCircleColor(R.color.colorAccent)
                                    .tintTarget(false)
                                    .titleTextSize(18)
                                    .descriptionTextSize(16)
                                    .drawShadow(true)
                                    .cancelable(false),
                            TapTarget.forView(v.findViewById(R.id.text_button), getString(R.string.tap_target_title_2), getString(R.string.tap_target_message_2))
                                    .transparentTarget(true)
                                    .tintTarget(false)
                                    .outerCircleColor(R.color.colorAccent)
                                    .tintTarget(false)
                                    .titleTextSize(18)
                                    .descriptionTextSize(16)
                                    .drawShadow(true)
                                    .cancelable(false),
                            TapTarget.forView(v.findViewById(R.id.back_utton), getString(R.string.tap_target_title_3), getString(R.string.tap_target_message_3))
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
