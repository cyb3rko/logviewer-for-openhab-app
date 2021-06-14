package com.cyb3rko.logviewerforopenhab.appintro

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyb3rko.logviewerforopenhab.ANALYTICS_COLLECTION
import com.cyb3rko.logviewerforopenhab.CRASHLYTICS_COLLECTION
import com.cyb3rko.logviewerforopenhab.SHARED_PREFERENCE
import com.cyb3rko.logviewerforopenhab.databinding.FragmentAppintro4Binding

class AppIntro4thFragment : Fragment() {
    private var _binding: FragmentAppintro4Binding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppintro4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mySPR = requireContext().getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val editor = mySPR.edit()

        binding.analyticsCheck.setOnCheckedChangeListener { _, b ->
            editor.putBoolean(ANALYTICS_COLLECTION, b).apply()
        }

        binding.crashlyticsCheck.setOnCheckedChangeListener { _, b ->
            editor.putBoolean(CRASHLYTICS_COLLECTION, b).apply()
        }
    }

    companion object {
        fun newInstance() : AppIntro4thFragment {
            return AppIntro4thFragment()
        }
    }
}