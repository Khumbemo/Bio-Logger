package com.biologger.forest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import com.biologger.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ForestHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forest_home, container, false)

        // 1. Initialize Survey Site Spinner
        val spinnerSite = view.findViewById<Spinner>(R.id.spinnerSite)
        val sites = listOf("North Plot A", "East Boundary", "Riparian Zone", "High Altitude Ridge")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sites)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSite.adapter = adapter

        // 2. Setup Tool Cards
        val cardConfigs = listOf(
            R.id.cardTreeMeasurement to R.id.action_forestHomeFragment_to_treeMeasurementFragment,
            R.id.cardBasalArea to R.id.action_forestHomeFragment_to_basalAreaFragment,
            R.id.cardStandDensity to R.id.action_forestHomeFragment_to_standDensityFragment,
            R.id.cardBiomass to R.id.action_forestHomeFragment_to_biomassFragment,
            R.id.cardGpsAndMapping to R.id.action_forestHomeFragment_to_gpsAndMappingFragment,
            R.id.cardDisturbanceIndex to R.id.action_forestHomeFragment_to_disturbanceIndexFragment
        )

        for (config in cardConfigs) {
            val (cardId, actionId) = config
            view.findViewById<MaterialCardView>(cardId)?.let { card ->
                card.setOnClickListener {
                    findNavController().navigate(actionId)
                }
                card.addPressAnimation()
            }
        }

        // 3. Setup Submit Button
        view.findViewById<MaterialButton>(R.id.btnSubmitContext).setOnClickListener {
            Toast.makeText(requireContext(), "Survey context locked for session", Toast.LENGTH_SHORT).show()
        }

        val scrollView = view.findViewById<NestedScrollView>(R.id.forestScrollView)
        scrollView?.let { setupBottomNavHideOnScroll(it) }

        return view
    }

    private fun setupBottomNavHideOnScroll(scrollView: NestedScrollView) {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav) ?: return
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
