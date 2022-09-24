package com.cyb3rko.logviewerforopenhab.appintro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyb3rko.logviewerforopenhab.*
import com.cyb3rko.logviewerforopenhab.PRIVACY_POLICY
import com.cyb3rko.logviewerforopenhab.TERMS_OF_USE
import com.cyb3rko.logviewerforopenhab.databinding.FragmentAppintro3Binding
import com.cyb3rko.logviewerforopenhab.showLicenseDialog
import com.github.appintro.SlidePolicy

class AppIntro3rdFragment : Fragment(), SlidePolicy {
    private var _binding: FragmentAppintro3Binding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppintro3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.termsOfUseButton.setOnClickListener { showLicenseDialog(context, TERMS_OF_USE) }
        binding.privacyPolicyButton.setOnClickListener { showLicenseDialog(context, PRIVACY_POLICY) }
    }

    override val isPolicyRespected: Boolean
        get() = (binding.termsOfUseCheck.isChecked && binding.privacyPolicyCheck.isChecked)

    override fun onUserIllegallyRequestedNextPage() {
        showToast(getString(R.string.intro_fragment3_toast))
    }

    companion object {
        fun newInstance() : AppIntro3rdFragment {
            return AppIntro3rdFragment()
        }
    }
}