package com.cyb3rko.logviewerforopenhab.appintro

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.cyb3rko.logviewerforopenhab.R
import com.github.appintro.SlidePolicy
import es.dmoral.toasty.Toasty

class AppIntro3rdFragment : Fragment(), SlidePolicy {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View = inflater.inflate(R.layout.fragment_appintro3, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button1 = view.findViewById(R.id.terms_of_use_button)
        button2 = view.findViewById(R.id.privacy_policy_button)
        checkBox1 = view.findViewById(R.id.terms_of_use_check)
        checkBox2 = view.findViewById(R.id.privacy_policy_check)

        button1.setOnClickListener { showDialog("terms_of_use") }
        button2.setOnClickListener { showDialog("privacy_policy") }
    }

    private fun showDialog(type: String) {
        MaterialDialog(requireContext(), BottomSheet()).show {
            message(0, Html.fromHtml(context.assets.open("$type.html").bufferedReader().use { it.readText() })) {
                messageTextView.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    override val isPolicyRespected: Boolean
        get() = (checkBox1.isChecked && checkBox2.isChecked)

    override fun onUserIllegallyRequestedNextPage() {
        Toasty.error(requireContext(), "To continue you have to agree to both", Toasty.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() : AppIntro3rdFragment {
            return AppIntro3rdFragment()
        }
    }
}