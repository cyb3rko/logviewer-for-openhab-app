package com.cyb3rko.logviewerforopenhab.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.cyb3rko.logviewerforopenhab.*
import com.cyb3rko.logviewerforopenhab.FIRST_START_WEB
import com.cyb3rko.logviewerforopenhab.LINK
import com.cyb3rko.logviewerforopenhab.ORIENTATION
import com.cyb3rko.logviewerforopenhab.SHARED_PREFERENCE
import com.cyb3rko.logviewerforopenhab.TEXTSIZE_AUTO
import com.cyb3rko.logviewerforopenhab.TEXTSIZE_LANDSCAPE
import com.cyb3rko.logviewerforopenhab.TEXTSIZE_PORTRAIT
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.mardous.discreteseekbar.DiscreteSeekBar
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_web_view.*

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

        mySPR = v.context.getSharedPreferences(SHARED_PREFERENCE, 0)
        editor = mySPR.edit()
        editor.apply()

        webSettings = webView.settings
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                view_button.visibility = View.GONE
                text_button.visibility = View.GONE
                webView.visibility = View.GONE
                animation_view.visibility = View.VISIBLE
                when (errorCode) {
                    -8 -> {
                        animation_view.setAnimation("timeout.json")
                        animation_desc.text = getString(R.string.webview_error_timeout)
                    }
                    -6 -> {
                        animation_view.setAnimation("connection.json")
                        animation_desc.text = if (description.contains("REFUSED")) {
                            getString(R.string.webview_error_connection_1)
                        } else {
                            getString(R.string.webview_error_connection_2)
                        }
                    }
                    -4 -> {
                        animation_view.setAnimation("auth.json")
                        animation_desc.text = getString(R.string.webview_error_authentication)
                    }
                    -2 -> {
                        if (description.contains("INTERNET")) {
                            animation_view.setAnimation("internet.json")
                            animation_desc.text = getString(R.string.webview_error_internet)
                        } else {
                            val host = mySPR.getString(LINK, "")!!.drop(7).split(":")[0]
                            animation_view.setAnimation("host.json")
                            animation_desc.text = getString(R.string.webview_error_host, host)
                        }
                    }
                    -1 -> {
                        if (description.contains("UNSAFE_PORT")) {
                            val port = mySPR.getString(LINK, "")!!.drop(7).split(":")[1]
                            animation_view.setAnimation("secure.json")
                            animation_desc.text = getString(R.string.webview_error_port, port)
                        } else {
                            animation_view.setAnimation("error.json")
                            animation_desc.text = getString(R.string.webview_error_unknown)
                            logUnknownError(errorCode, description)
                        }
                    }
                    else -> {
                        animation_view.setAnimation("error.json")
                        animation_desc.text = getString(R.string.webview_error_unknown)
                        logUnknownError(errorCode, description)
                    }
                }
                retry_button.visibility = View.VISIBLE
                retry_button.setOnClickListener {
                    findNavController().navigate(R.id.nav_webview)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (mySPR.getBoolean(HIDE_TOPBAR, false)) {
                    view?.loadUrl("javascript:document.getElementsByClassName(\"topbar navbar navbar-inverse navbar-fixed-top\")[0].setAttribute" +
                            "(\"style\",\"display:none;\");")
                }
            }
        }
        webSettings.javaScriptEnabled = true
        v.isVerticalScrollBarEnabled = false
        setTouchable(false)

        textSizeType = when (mySPR.getString(ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT.toString())?.toInt()) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> TEXTSIZE_AUTO
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> TEXTSIZE_LANDSCAPE
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> TEXTSIZE_PORTRAIT
            else -> { "" }
        }
        webSettings.textZoom = mySPR.getInt(textSizeType, 60)

        setToolbarVisibility(activity, View.GONE)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.loadUrl(mySPR.getString(LINK, "")!!)

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (viewLocked) {
                    webView.scrollBy(0, 10000)
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 500)
        setViewButtonClickListener()
        setTextButtonClickListener(view)
        setBackButtonClickListener()
        showTapTargetSequence(view)
    }

    private fun logUnknownError(errorCode: Int, description: String) {
        Firebase.analytics.logEvent("webview_error") {
            param(FirebaseAnalytics.Param.ITEM_ID, errorCode.toString())
            param(FirebaseAnalytics.Param.VALUE, description)
        }
    }

    private fun setViewButtonClickListener() {
        viewButton.setOnClickListener { view ->
            if (viewLocked) {
                viewButton.setImageResource(R.drawable._ic_lock_2)
                viewLocked = false
                setTouchable(true)

                Toasty.info(view.context, getString(R.string.lock_button_1), Toasty.LENGTH_SHORT).show()
            } else {
                viewButton.setImageResource(R.drawable._ic_lock)
                viewLocked = true
                setTouchable(false)

                webView.scrollBy(0, 10000)

                Toasty.info(view.context, getString(R.string.lock_button_2), Toasty.LENGTH_SHORT).show()
            }
        }
    }

    private fun setTextButtonClickListener(v: View) {
        textButton.setOnClickListener {
            val content = LinearLayout(v.context)
            content.orientation = LinearLayout.VERTICAL
            val sizeView = TextView(v.context)
            val discreteSeekBar = DiscreteSeekBar(v.context)
            val currentTextSize = mySPR.getInt(textSizeType, 60)

            sizeView.text = String.format(getString(R.string.text_size_dialog_text), currentTextSize, currentTextSize)
            sizeView.textSize = 18f
            sizeView.setPadding(24, 24, 24, 50)
            sizeView.gravity = Gravity.CENTER_HORIZONTAL
            content.addView(sizeView)
            discreteSeekBar.max = 100
            discreteSeekBar.min = 1
            discreteSeekBar.progress = webSettings.textZoom
            discreteSeekBar.setPadding(50, 0, 50, 50)
            discreteSeekBar.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
                override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                    // nothing to clean up (for PMD)
                }

                override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {
                    // nothing to clean up (for PMD)
                }

                override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {
                    sizeView.text = String.format(getString(R.string.text_size_dialog_text), currentTextSize, discreteSeekBar.progress)
                }
            })
            content.addView(discreteSeekBar)

            MaterialDialog(requireContext()).show {
                title(R.string.text_size_dialog_title)
                customView(0, content)
                positiveButton(R.string.text_size_dialog_button_1) {
                    val textSize = discreteSeekBar.progress
                    webSettings.textZoom = textSize
                    editor.putInt(textSizeType, textSize).apply()
                    Toasty.info(v.context, getString(R.string.text_size_changed) + textSize, Toasty.LENGTH_SHORT).show()
                }
                negativeButton(R.string.text_size_dialog_button_2)
            }
        }
    }

    private fun setBackButtonClickListener() {
        backButton.setOnClickListener {
            findNavController().navigate(R.id.nav_menu)
            setToolbarVisibility(activity, View.VISIBLE)
        }
    }

    private fun showTapTargetSequence(v: View) {
        if (mySPR.getBoolean(FIRST_START_WEB, true)) {
            TapTargetSequence(activity)
                .targets(
                    TapTarget.forView(
                        v.findViewById(R.id.view_button),
                        getString(R.string.tap_target_title_1),
                        getString(R.string.tap_target_message_1)
                    )
                        .transparentTarget(true)
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorAccent)
                        .tintTarget(false)
                        .titleTextSize(18)
                        .descriptionTextSize(16)
                        .drawShadow(true)
                        .cancelable(false),
                    TapTarget.forView(
                        v.findViewById(R.id.text_button),
                        getString(R.string.tap_target_title_2),
                        getString(R.string.tap_target_message_2)
                    )
                        .transparentTarget(true)
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorAccent)
                        .tintTarget(false)
                        .titleTextSize(18)
                        .descriptionTextSize(16)
                        .drawShadow(true)
                        .cancelable(false),
                    TapTarget.forView(
                        v.findViewById(R.id.back_button),
                        getString(R.string.tap_target_title_3),
                        getString(R.string.tap_target_message_3)
                    )
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
                        editor.putBoolean(FIRST_START_WEB, false).apply()
                    }

                    override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                        // nothing to clean up (for PMD)
                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        // nothing to clean up (for PMD)
                    }
                })
                .start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchable(b: Boolean) {
        if (b) {
            webView.setOnTouchListener { _, _ -> false }
        } else {
            webView.setOnTouchListener { _, _ -> true }
        }
    }
}