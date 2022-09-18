package com.cyb3rko.logviewerforopenhab.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyb3rko.abouticons.AboutIcons
import com.cyb3rko.logviewerforopenhab.R

class IconCreditsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return context?.let { AboutIcons(it, R.drawable::class.java, parentFragmentManager).get() }
    }
}