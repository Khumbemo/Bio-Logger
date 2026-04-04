package com.biologger

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

/**
 * Note: This fragment is being replaced by ToolsFragment in the new 3-tab architecture.
 * Keeping it for reference or as a detailed tools view if needed.
 */
class MainDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_dashboard, container, false)

        val cardForest = view.findViewById<MaterialCardView>(R.id.cardForest)
        val cardGreenhouse = view.findViewById<MaterialCardView>(R.id.cardGreenhouse)
        val cardGarden = view.findViewById<MaterialCardView>(R.id.cardGarden)
        val cardNotes = view.findViewById<MaterialCardView>(R.id.cardNotes)

        cardForest.setOnClickListener {
            findNavController().navigate(R.id.toolsFragment) // Redirect to tools
        }
        cardForest.addPressAnimation()

        return view
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
