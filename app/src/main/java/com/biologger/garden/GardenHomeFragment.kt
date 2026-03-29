package com.biologger.garden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.biologger.R
import com.google.android.material.card.MaterialCardView

class GardenHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_garden_home, container, false)

        view.findViewById<MaterialCardView>(R.id.cardSeasonalPlanner).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_seasonalPlannerFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardCompanionPlanting).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_companionPlantingFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardSoilAmendment).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_soilAmendmentFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardBedLayout).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_bedLayoutFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardWateringSchedule).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_wateringScheduleFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardCompostTracker).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_compostTrackerFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardPestId).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_pestIdFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardYieldRecorder).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_yieldRecorderFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardYieldEstimation).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_yieldEstimationFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardPlantWiki).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_plantWikiFragment)
        }
        view.findViewById<MaterialCardView>(R.id.cardPlanningCalendar).setOnClickListener {
            findNavController().navigate(R.id.action_gardenHomeFragment_to_planningCalendarFragment)
        }

        return view
    }
}
