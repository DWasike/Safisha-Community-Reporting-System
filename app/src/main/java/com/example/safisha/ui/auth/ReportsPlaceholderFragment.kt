package com.example.safisha.ui.auth

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ReportsPlaceholderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val showAll = arguments?.getBoolean("showAll") ?: false
        return TextView(requireContext()).apply {
            gravity = Gravity.CENTER
            textSize = 18f
            text = if (showAll) {
                "All reports (admin)\nMember 4's list goes here"
            } else {
                "My reports\nMember 4's list goes here"
            }
        }
    }
}