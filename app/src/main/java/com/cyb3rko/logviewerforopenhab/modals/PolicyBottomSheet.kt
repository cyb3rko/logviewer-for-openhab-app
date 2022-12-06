package com.cyb3rko.logviewerforopenhab.modals

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyb3rko.logviewerforopenhab.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView

class PolicyBottomSheet(private val policyType: String) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_policy, container, false)
        val messageView = view.findViewById<MaterialTextView>(R.id.message_view)
        messageView.text = Html.fromHtml(
            requireContext().assets.open("$policyType.html").bufferedReader().use { it.readText() }
        )
        messageView.movementMethod = LinkMovementMethod.getInstance()
        return view
    }

    companion object {
        const val TAG = "Policy Bottom Sheet"
    }
}
