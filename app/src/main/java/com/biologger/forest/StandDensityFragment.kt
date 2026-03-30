package com.biologger.forest

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.StandDensityResult
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.pow

class StandDensityFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stand_density, container, false)

        val editPlotId = view.findViewById<TextInputEditText>(R.id.editPlotId)
        val editTreesHa = view.findViewById<TextInputEditText>(R.id.editTreesHa)
        val editQmd = view.findViewById<TextInputEditText>(R.id.editQmd)
        val spinnerSpecies = view.findViewById<AutoCompleteTextView>(R.id.spinnerSpeciesGroup)
        val editMaxSdi = view.findViewById<TextInputEditText>(R.id.editMaxSdi)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalculateSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        spinnerSpecies.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Conifers", "Broadleaves", "Mixed", "Custom")))
        spinnerSpecies.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) editMaxSdi.setText("1000") else if (position == 1) editMaxSdi.setText("800")
        }

        btnCalc.setOnClickListener {
            val nHa = editTreesHa.text.toString().toDoubleOrNull() ?: 0.0
            val qmd = editQmd.text.toString().toDoubleOrNull() ?: 1.0
            val maxSdi = editMaxSdi.text.toString().toDoubleOrNull() ?: 1.0
            val sdi = nHa * (25.4 / qmd).pow(1.605)
            val rd = (sdi / maxSdi) * 100.0

            val (zone, color) = when {
                rd < 25 -> "Low density" to Color.GREEN
                rd < 35 -> "Moderate density" to Color.YELLOW
                rd < 60 -> "Full site occupancy" to Color.rgb(255, 165, 0)
                else -> "Overcrowded" to Color.RED
            }

            view.findViewById<TextView>(R.id.resSdiValue).text = "SDI: %.1f".format(sdi)
            view.findViewById<TextView>(R.id.resRdPercent).text = "Relative Density: %.1f%%".format(rd)
            view.findViewById<View>(R.id.viewZoneIndicator).setBackgroundColor(color)
            view.findViewById<TextView>(R.id.resZoneLabel).text = zone
            cardResults.visibility = View.VISIBLE

            viewModel.insertStandDensity(StandDensityResult(
                plotId = editPlotId.text.toString(),
                treesPerHa = nHa, qmd = qmd, speciesGroup = spinnerSpecies.text.toString(),
                maxSdi = maxSdi, sdi = sdi, relativeDensity = rd, competitionZone = zone,
                latitude = null, longitude = null
            ))
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(0, 0, 0, maxOf(imeHeight, navHeight))
            insets
        }
        view.setOnClickListener {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        return view
    }
}
