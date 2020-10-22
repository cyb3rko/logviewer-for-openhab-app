package com.cyb3rko.logviewerforopenhab

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TermsOfUseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_terms_of_use, container, false)

        val textViews = arrayOfNulls<TextView>(7)
        for (i in 1..7) {
            textViews[i - 1] = view.findViewById(resources.getIdentifier("textView$i", "id", context?.packageName))
            textViews[i - 1]?.text = (resources.getStringArray(R.array.terms_of_use)[i - 1])
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse(
                String.format(
                    getString(R.string.mail_info), getString(R.string.app_name),
                    resources.getStringArray(R.array.terms_of_use)[0]
                )
            )
            mailIntent.data = data
            startActivity(Intent.createChooser(mailIntent, getString(R.string.send_mail)))
        }

        return view
    }
}