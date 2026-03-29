package com.biologger.greenhouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.biologger.R
import com.google.android.material.card.MaterialCardView

class GreenhouseHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_greenhouse_home, container, false)

        view.findViewById<MaterialCardView>(R.id.cardPotExperiment).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_potExperimentFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardPotLogger).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_potExperimentLoggerFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardClimateLog).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_climateLogFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardGerminationTracker).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_germinationTrackerFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardFertilizerCalculator).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_fertilizerCalculatorFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardIrrigationScheduler).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_irrigationSchedulerFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardPestDisease).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_pestDiseaseLogFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardHarvestRecorder).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_harvestRecorderFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardGrowthRate).setOnClickListener {
            findNavController().navigate(R.id.action_greenhouseHomeFragment_to_growthRateFragment)
        }

        return view
    }
}
