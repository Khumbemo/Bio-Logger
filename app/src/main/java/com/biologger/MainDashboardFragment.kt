package com.biologger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

class MainDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_dashboard, container, false)

        view.findViewById<MaterialCardView>(R.id.cardForest).setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_forestHomeFragment)
        }

        view.findViewById<MaterialCardView>(R.id.cardGreenhouse).setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_greenhouseHomeFragment)
        }

        view.findViewById<MaterialCardView>(R.id.cardGarden).setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_gardenHomeFragment)
        }

        view.findViewById<MaterialCardView>(R.id.cardNotes).setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_noteVaultHomeFragment)
        }

        return view
    }
}
