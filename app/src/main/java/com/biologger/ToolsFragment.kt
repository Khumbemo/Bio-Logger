package com.biologger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.view.animation.DecelerateInterpolator
import com.google.android.material.card.MaterialCardView

class ToolsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)

        view.findViewById<MaterialCardView>(R.id.cardForest).setOnClickListener {
            findNavController().navigate(R.id.action_toolsFragment_to_forestHomeFragment)
        }

        view.findViewById<MaterialCardView>(R.id.cardGreenhouse).setOnClickListener {
            findNavController().navigate(R.id.action_toolsFragment_to_greenhouseHomeFragment)
        }

        view.findViewById<MaterialCardView>(R.id.cardGarden).setOnClickListener {
            findNavController().navigate(R.id.action_toolsFragment_to_gardenHomeFragment)
        }

        view.findViewById<MaterialCardView>(R.id.cardNotes).setOnClickListener {
            findNavController().navigate(R.id.action_toolsFragment_to_noteVaultHomeFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cards = listOf<View>(
            view.findViewById(R.id.cardForest),
            view.findViewById(R.id.cardGreenhouse),
            view.findViewById(R.id.cardGarden),
            view.findViewById(R.id.cardNotes)
        )
        
        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationY = 100f
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * 100L)
                .setDuration(500)
                .setInterpolator(DecelerateInterpolator(2f))
                .start()
        }
    }
}
