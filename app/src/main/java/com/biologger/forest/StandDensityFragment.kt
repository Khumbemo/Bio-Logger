package com.biologger.forest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.biologger.R
import androidx.navigation.fragment.findNavController

class StandDensityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_placeholder, container, false)
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnBackPlaceholder).setOnClickListener { findNavController().popBackStack() }
        return view
    }
}
