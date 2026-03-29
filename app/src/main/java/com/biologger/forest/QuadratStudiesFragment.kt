package com.biologger.forest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.biologger.R
<<<<<<< HEAD
import androidx.navigation.fragment.findNavController
=======
>>>>>>> master

class QuadratStudiesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_placeholder, container, false)
<<<<<<< HEAD
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnBackPlaceholder).setOnClickListener { findNavController().popBackStack() }
=======
        view.findViewById<TextView>(R.id.textPlaceholder).text = "QuadratStudies — Loading"
>>>>>>> master
        return view
    }
}
