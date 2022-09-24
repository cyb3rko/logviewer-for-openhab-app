package com.cyb3rko.logviewerforopenhab.fragments

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
import com.cyb3rko.logviewerforopenhab.R
import com.cyb3rko.logviewerforopenhab.openUrl
import com.google.android.material.card.MaterialCardView

class AnimationCreditsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myContext = requireContext()
        val information = listOf(
            Triple("error lottie", "koh", "https://lottiefiles.com/17373-error-lottie"),
            Triple("Face scanning", "Aneesh Ravi", "https://lottiefiles.com/4432-face-scanning"),
            Triple("Hosting", "Jesús Suárez", "https://lottiefiles.com/29413-hosting"),
            Triple("No connection", "Soaga Kingsley Tobi", "https://lottiefiles.com/31490-no-connection"),
            Triple("No Internet", "Mehran", "https://lottiefiles.com/28813-no-internet"),
            Triple("Secure lock", "LottieFiles", "https://lottiefiles.com/11476-secure-lock"),
            Triple("Timer Progress Animation", "Vishalpari Goswami", "https://lottiefiles.com/35812-timer-progress-animation")
        )
        val view = ScrollView(myContext)
        val linearLayout = LinearLayout(myContext)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPaddingRelative(50, 50, 50, 0)
        information.forEach {
            val textView = TextView(myContext)
            textView.textSize = 18f
            textView.setPaddingRelative(50, 50, 50, 50)
            val text = getString(R.string.about_animations, it.first, it.second)
            val spannableString = SpannableString(text)
            val clickableSpan = object: ClickableSpan() {
                override fun onClick(widget: View) {
                    openUrl(it.third)
                }
            }
            spannableString.setSpan(
                clickableSpan,
                1,
                it.first.length + 1,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            textView.text = spannableString
            textView.movementMethod = LinkMovementMethod.getInstance()
            val card = MaterialCardView(myContext)
            card.setMargins(0, 0, 0, 20)
            card.addView(textView)
            linearLayout.addView(card)
        }
        view.addView(linearLayout)
        return view
    }

    private fun View.setMargins(start: Int, top: Int, end: Int, bottom: Int) {
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.MATCH_PARENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(start, top, end, bottom)
        this.layoutParams = layoutParams
        this.requestLayout()
    }
}
