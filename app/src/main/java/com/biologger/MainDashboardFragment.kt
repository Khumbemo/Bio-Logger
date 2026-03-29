package com.biologger

<<<<<<< HEAD
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
=======
import android.os.Bundle
import android.view.LayoutInflater
>>>>>>> master
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
<<<<<<< HEAD
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
=======
>>>>>>> master
import com.google.android.material.card.MaterialCardView

class MainDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_dashboard, container, false)

<<<<<<< HEAD
        val cardForest = view.findViewById<MaterialCardView>(R.id.cardForest)
        val cardGreenhouse = view.findViewById<MaterialCardView>(R.id.cardGreenhouse)
        val cardGarden = view.findViewById<MaterialCardView>(R.id.cardGarden)
        val cardNotes = view.findViewById<MaterialCardView>(R.id.cardNotes)

        cardForest.setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_forestHomeFragment)
        }
        cardForest.addPressAnimation()

        cardGreenhouse.setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_greenhouseHomeFragment)
        }
        cardGreenhouse.addPressAnimation()

        cardGarden.setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_gardenHomeFragment)
        }
        cardGarden.addPressAnimation()

        cardNotes.setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboardFragment_to_noteVaultHomeFragment)
        }
        cardNotes.addPressAnimation()

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
=======
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
>>>>>>> master
}
