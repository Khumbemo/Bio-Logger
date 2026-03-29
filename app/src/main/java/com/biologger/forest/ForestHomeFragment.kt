package com.biologger.forest

<<<<<<< HEAD
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
import com.biologger.MainActivity
import com.biologger.R
import com.google.android.material.bottomnavigation.BottomNavigationView
=======
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.biologger.R
>>>>>>> master
import com.google.android.material.card.MaterialCardView

class ForestHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forest_home, container, false)

<<<<<<< HEAD
        val cards = listOf(
            R.id.cardTreeMeasurement to R.id.action_forestHomeFragment_to_treeMeasurementFragment,
            R.id.cardBasalArea to R.id.action_forestHomeFragment_to_basalAreaFragment,
            R.id.cardStandDensity to R.id.action_forestHomeFragment_to_standDensityFragment,
            R.id.cardTimberVolume to R.id.action_forestHomeFragment_to_timberVolumeFragment,
            R.id.cardBiomass to R.id.action_forestHomeFragment_to_biomassFragment,
            R.id.cardTreeHealth to R.id.action_forestHomeFragment_to_treeHealthFragment,
            R.id.cardCrownCover to R.id.action_forestHomeFragment_to_crownCoverFragment,
            R.id.cardLitterfall to R.id.action_forestHomeFragment_to_litterfallFragment,
            R.id.cardQuadratStudies to R.id.action_forestHomeFragment_to_quadratStudiesFragment,
            R.id.cardTransectStudies to R.id.action_forestHomeFragment_to_transectStudiesFragment,
            R.id.cardGpsAndMapping to R.id.action_forestHomeFragment_to_gpsAndMappingFragment,
            R.id.cardEnvironmentalVariables to R.id.action_forestHomeFragment_to_environmentalVariablesFragment,
            R.id.cardDisturbanceIndex to R.id.action_forestHomeFragment_to_disturbanceIndexFragment,
            R.id.cardDigitalHerbarium to R.id.action_forestHomeFragment_to_digitalHerbariumFragment
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
=======
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
>>>>>>> master
}
