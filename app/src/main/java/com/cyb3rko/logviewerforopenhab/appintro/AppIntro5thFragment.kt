package com.cyb3rko.logviewerforopenhab.appintro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyb3rko.logviewerforopenhab.R
import com.cyb3rko.logviewerforopenhab.SHARED_PREFERENCE
import com.google.android.material.switchmaterial.SwitchMaterial

class AppIntro5thFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View = inflater.inflate(R.layout.fragment_appintro5, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mySPR = requireContext().getSharedPreferences(SHARED_PREFERENCE, 0)
        val editor = mySPR.edit()

        view.findViewById<SwitchMaterial>(R.id.updateSwitch).setOnCheckedChangeListener { _, b ->
            editor.putBoolean("autoUpdate", b).apply()
        }
    }

    companion object {
        fun newInstance() : AppIntro5thFragment {
            return AppIntro5thFragment()
        }
    }
}