package com.biologger.garden
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import com.biologger.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class GardenHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_garden_home, container, false)

        val cards = listOf(
            R.id.cardSeasonalPlanner to R.id.action_gardenHomeFragment_to_seasonalPlannerFragment,
            R.id.cardCompanionPlanting to R.id.action_gardenHomeFragment_to_companionPlantingFragment,
            R.id.cardSoilAmendment to R.id.action_gardenHomeFragment_to_soilAmendmentFragment,
            R.id.cardBedLayout to R.id.action_gardenHomeFragment_to_bedLayoutFragment,
            R.id.cardWateringSchedule to R.id.action_gardenHomeFragment_to_wateringScheduleFragment,
            R.id.cardCompostTracker to R.id.action_gardenHomeFragment_to_compostTrackerFragment,
            R.id.cardPestId to R.id.action_gardenHomeFragment_to_pestIdFragment,
            R.id.cardYieldRecorder to R.id.action_gardenHomeFragment_to_yieldRecorderFragment,
            R.id.cardYieldEstimation to R.id.action_gardenHomeFragment_to_yieldEstimationFragment,
            R.id.cardPlantWiki to R.id.action_gardenHomeFragment_to_plantWikiFragment,
            R.id.cardPlanningCalendar to R.id.action_gardenHomeFragment_to_planningCalendarFragment
        )

        cards.forEach { (cardId, actionId) ->
            val card = view.findViewById<MaterialCardView>(cardId)
            card.setOnClickListener {
                findNavController().navigate(actionId)
            }
            card.addPressAnimation()
        }

        val scrollView = view as? NestedScrollView
        scrollView?.let { setupBottomNavHideOnScroll(it) }

        return view
    }

    private fun setupBottomNavHideOnScroll(scrollView: NestedScrollView) {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        var lastScrollY = 0
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > lastScrollY + 10) {
                bottomNav.animate()
                    .translationY(bottomNav.height.toFloat())
                    .setDuration(200)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .start()
            } else if (scrollY < lastScrollY - 10) {
                bottomNav.animate()
                    .translationY(0f)
                    .setDuration(200)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .start()
            }
            lastScrollY = scrollY
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun View.addPressAnimation() {
        setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.96f)
                        .scaleY(0.96f)
                        .setDuration(120)
                        .setInterpolator(FastOutSlowInInterpolator())
                        .start()
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(120)
                        .setInterpolator(FastOutSlowInInterpolator())
                        .start()
                }
            }
            false
        }
    }
}
