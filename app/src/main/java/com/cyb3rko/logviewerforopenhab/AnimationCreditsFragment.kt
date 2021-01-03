package com.cyb3rko.logviewerforopenhab

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class AnimationCreditsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val information = listOf(
            Triple("error lottie", "koh", "https://lottiefiles.com/17373-error-lottie"),
            Triple("Face scanning", "Aneesh Ravi", "https://lottiefiles.com/4432-face-scanning"),
            Triple("Hosting", "Jesús Suárez", "https://lottiefiles.com/29413-hosting"),
            Triple("No connection", "Soaga Kingsley Tobi", "https://lottiefiles.com/31490-no-connection"),
            Triple("No Internet", "Mehran", "https://lottiefiles.com/28813-no-internet"),
            Triple("Secure lock", "LottieFiles", "https://lottiefiles.com/11476-secure-lock"),
            Triple("Timer Progress Animation", "Vishalpari Goswami", "https://lottiefiles.com/35812-timer-progress-animation")
        )
        val view = ScrollView(requireContext())
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL
        information.forEach {
            val textView = TextView(requireContext())
            textView.textSize = 18f
            textView.setPaddingRelative(40, 50, 40, 0)
            val text = "'${it.first}' animation by ${it.second}"
            val spannableString = SpannableString(text)
            val clickableSpan = object: ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.third)))
                }
            }
            spannableString.setSpan(clickableSpan, 1, 1 + it.first.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            textView.text = spannableString
            textView.movementMethod = LinkMovementMethod.getInstance()
            linearLayout.addView(textView)
        }
        view.addView(linearLayout)
        return view
    }
}