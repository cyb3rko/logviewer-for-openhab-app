package com.cyb3rko.logviewerforopenhab

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.floatingactionbutton.FloatingActionButton
import es.dmoral.toasty.Toasty
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar.OnProgressChangeListener

class WebViewFragment : Fragment() {
    private lateinit var backButton: FloatingActionButton
    private lateinit var textButton: FloatingActionButton
    private lateinit var viewButton: FloatingActionButton
    private lateinit var mySPR: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var textSizeType: String
    private lateinit var webSettings: WebSettings
    private lateinit var webView: WebView
    private var viewLocked = true

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = v.findViewById(R.id.webview)
        viewButton = v.findViewById(R.id.view_button)
        textButton = v.findViewById(R.id.text_button)
        backButton = v.findViewById(R.id.back_button)

        // load save file and its editor
        mySPR = v.context.getSharedPreferences("Safe", 0)
        editor = mySPR.edit()
        editor.apply()

        // adapt settings for webview
        webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        v.isVerticalScrollBarEnabled = false
        setTouchable(false)

        // restore text size (according to orientation)
        textSizeType = when (mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> "textSizeAuto"
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> "textSizeLandscape"
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> "textSizePortrait"
            else -> { "" }
        }
        webSettings.textZoom = mySPR.getInt(textSizeType, 60)

        // open link
        webView.loadUrl(mySPR.getString("link", "")!!)

        // permanent scroll
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (viewLocked) {
                    webView.scrollBy(0, 10000)
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 500)
        setViewButtonClickListener()
        setTextButtonClickListener(v)
        setBackButtonClickListener()
        showTapTargetSequence(v)
        setToolbarVisibility(activity, View.GONE)
        return v
    }

    // onClickListener for viwe button
    private fun setViewButtonClickListener() {
        viewButton.setOnClickListener { view ->
            // if scrolling locked
            if (viewLocked) {
                viewButton.setImageResource(R.drawable._icon_lock_2)
                viewLocked = false
                setTouchable(true)

                // show toast
                Toasty.info(view.context, getString(R.string.lock_button_1), Toasty.LENGTH_SHORT).show()
            } else {
                viewButton.setImageResource(R.drawable._icon_lock)
                viewLocked = true
                setTouchable(false)

                //scroll to bottom
                webView.scrollBy(0, 10000)

                // show toast
                Toasty.info(view.context, getString(R.string.lock_button_2), Toasty.LENGTH_SHORT).show()
            }
        }
    }

    // dialog to change text size
    private fun setTextButtonClickListener(v: View) {
        textButton.setOnClickListener { view ->
            // create dialog builder
            val builder = AlertDialog.Builder(v.context)

            // current text size
            val currentTextSize = mySPR.getInt(textSizeType, 60)
            // add textview for showing text size
            val sizeView = TextView(v.context)
            // add seekbar for choosing text size
            val discreteSeekBar = DiscreteSeekBar(v.context)

            // create title
            val titleView = TextView(v.context)
            titleView.setText(R.string.text_size_dialog_title)
            titleView.textSize = 22f
            titleView.typeface = Typeface.DEFAULT_BOLD
            titleView.setPadding(32, 32, 32, 32)
            titleView.gravity = Gravity.CENTER_HORIZONTAL
            val content = LinearLayout(v.context)
            content.orientation = LinearLayout.VERTICAL

            // set size view
            sizeView.text = String.format(getString(R.string.text_size_dialog_text), currentTextSize, currentTextSize)
            sizeView.textSize = 18f
            sizeView.setPadding(24, 24, 24, 50)
            sizeView.gravity = Gravity.CENTER_HORIZONTAL
            content.addView(sizeView)

            // set seekbar
            discreteSeekBar.max = 100
            discreteSeekBar.min = 1
            discreteSeekBar.progress = webSettings.textZoom
            discreteSeekBar.setPadding(50, 0, 50, 50)
            discreteSeekBar.setOnProgressChangeListener(object : OnProgressChangeListener {
                override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                    // nothing to clean up (for PMD)
                }

                override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {
                    // nothing to clean up (for PMD)
                }

                // if user stops touching seekbar
                override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {
                    sizeView.text = String.format(getString(R.string.text_size_dialog_text), currentTextSize, discreteSeekBar.progress)
                }
            })
            content.addView(discreteSeekBar)

            // create dialog
            builder.setCustomTitle(titleView)
                .setView(content) // add right button
                .setPositiveButton(getString(R.string.text_size_dialog_button_1)) { _, _ ->
                    val textSize = discreteSeekBar.progress

                    // change text size
                    webSettings.textZoom = textSize
                    // store new size
                    editor.putInt(textSizeType, textSize).apply()
                    // show toast
                    Toasty.success(view.context, getString(R.string.text_size_changed) + textSize, Toasty.LENGTH_SHORT).show()
                } // add left button
                .setNegativeButton(getString(R.string.text_size_dialog_button_2)) { _, _ ->
                    // nothing to clean up (for PMD)
                } // show dialog
                .create().show()
        }
    }

    private fun setBackButtonClickListener() {
        backButton.setOnClickListener {
            findNavController().navigate(R.id.nav_menu)
            setToolbarVisibility(activity, View.VISIBLE)
        }
    }

    // taptargetsequence to explain buttons
    private fun showTapTargetSequence(v: View) {
        // if first log visit
        if (mySPR.getBoolean("firstStartWeb", true)) {
            TapTargetSequence(activity)
                .targets( // first target (view button)
                    TapTarget.forView(
                        v.findViewById(R.id.view_button),
                        getString(R.string.tap_target_title_1),
                        getString(R.string.tap_target_message_1)
                    ) // many settings
                        .transparentTarget(true)
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorAccent)
                        .tintTarget(false)
                        .titleTextSize(18)
                        .descriptionTextSize(16)
                        .drawShadow(true)
                        .cancelable(false),  // first target (text button)
                    TapTarget.forView(
                        v.findViewById(R.id.text_button),
                        getString(R.string.tap_target_title_2),
                        getString(R.string.tap_target_message_2)
                    ) // many settings
                        .transparentTarget(true)
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorAccent)
                        .tintTarget(false)
                        .titleTextSize(18)
                        .descriptionTextSize(16)
                        .drawShadow(true)
                        .cancelable(false),  // first target (back button)
                    TapTarget.forView(
                        v.findViewById(R.id.back_button),
                        getString(R.string.tap_target_title_3),
                        getString(R.string.tap_target_message_3)
                    ) // many settings
                        .transparentTarget(true)
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorAccent)
                        .tintTarget(false)
                        .titleTextSize(18)
                        .descriptionTextSize(16)
                        .drawShadow(true)
                        .cancelable(false)
                ).listener(object : TapTargetSequence.Listener {
                    override fun onSequenceFinish() {
                        editor.putBoolean("firstStartWeb", false).apply()
                    }

                    // on every single new target
                    override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                        // nothing to clean up (for PMD)
                    }

                    // if sequence has been canceled
                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        // nothing to clean up (for PMD)
                    }
                }) // start sequence
                .start()
        }
    }

    // switch touchability of log
    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchable(b: Boolean) {
        if (b) {
            webView.setOnTouchListener { _, _ -> false }
        } else {
            webView.setOnTouchListener { _, _ -> true }
        }
    }
}