package com.cyb3rko.logviewerforopenhab.appintro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.cyb3rko.logviewerforopenhab.R

class AppIntro4thFragment : Fragment() {

    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View = inflater.inflate(R.layout.fragment_appintro4, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBox1 = view.findViewById(R.id.analytics_check)
        checkBox2 = view.findViewById(R.id.crashlytics_check)

        val mySPR = requireContext().getSharedPreferences("Safe2", 0)
        val editor = mySPR.edit()

        checkBox1.setOnCheckedChangeListener { _, b ->
            editor.putBoolean("analyticsCollection", b).apply()
        }

        checkBox2.setOnCheckedChangeListener { _, b ->
            editor.putBoolean("crashlyticsCollection", b).apply()
        }
    }

    companion object {
        fun newInstance() : AppIntro4thFragment {
            return AppIntro4thFragment()
        }
    }
}