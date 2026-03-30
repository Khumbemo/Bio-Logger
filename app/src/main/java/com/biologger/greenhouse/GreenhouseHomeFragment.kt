package com.biologger.greenhouse

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import com.biologger.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class GreenhouseHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_greenhouse_home, container, false)

        val cards = listOf(
            R.id.cardPotExperiment to R.id.action_greenhouseHomeFragment_to_potExperimentFragment,
            R.id.cardPotLogger to R.id.action_greenhouseHomeFragment_to_potExperimentLoggerFragment,
            R.id.cardClimateLog to R.id.action_greenhouseHomeFragment_to_climateLogFragment,
            R.id.cardGerminationTracker to R.id.action_greenhouseHomeFragment_to_germinationTrackerFragment,
            R.id.cardFertilizerCalculator to R.id.action_greenhouseHomeFragment_to_fertilizerCalculatorFragment,
            R.id.cardIrrigationScheduler to R.id.action_greenhouseHomeFragment_to_irrigationSchedulerFragment,
            R.id.cardPestDisease to R.id.action_greenhouseHomeFragment_to_pestDiseaseLogFragment,
            R.id.cardHarvestRecorder to R.id.action_greenhouseHomeFragment_to_harvestRecorderFragment,
            R.id.cardGrowthRate to R.id.action_greenhouseHomeFragment_to_growthRateFragment
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
