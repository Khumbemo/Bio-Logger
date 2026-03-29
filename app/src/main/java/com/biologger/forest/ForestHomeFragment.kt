package com.biologger.forest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.biologger.R
import com.google.android.material.card.MaterialCardView

class ForestHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forest_home, container, false)

        view.findViewById<MaterialCardView>(R.id.cardTreeMeasurement).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_treeMeasurementFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardBasalArea).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_basalAreaFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardStandDensity).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_standDensityFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardTimberVolume).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_timberVolumeFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardBiomass).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_biomassFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardTreeHealth).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_treeHealthFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardCrownCover).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_crownCoverFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardLitterfall).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_litterfallFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardQuadratStudies).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_quadratStudiesFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardTransectStudies).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_transectStudiesFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardGpsAndMapping).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_gpsAndMappingFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardEnvironmentalVariables).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_environmentalVariablesFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardDisturbanceIndex).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_disturbanceIndexFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardDigitalHerbarium).setOnClickListener {
            findNavController().navigate(R.id.action_forestHomeFragment_to_digitalHerbariumFragment)
        }

        return view
    }
}
