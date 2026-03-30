package com.biologger.garden
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.YieldEstimate
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class YieldEstimationFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_yield_estimation, container, false)
        val editCrop = view.findViewById<TextInputEditText>(R.id.editCrop)
        val editArea = view.findViewById<TextInputEditText>(R.id.editArea)
        val editCount = view.findViewById<TextInputEditText>(R.id.editPlantCount)
        val editFruits = view.findViewById<TextInputEditText>(R.id.editFruitsPerPlant)
        val editWeight = view.findViewById<TextInputEditText>(R.id.editAvgFruitWeight)
        val btnEstimate = view.findViewById<MaterialButton>(R.id.btnEstimate)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnEstimate.setOnClickListener {
            val crop = editCrop.text.toString()
            val area = editArea.text.toString().toDoubleOrNull() ?: 1.0
            val count = editCount.text.toString().toIntOrNull() ?: 1
            val fruits = editFruits.text.toString().toDoubleOrNull() ?: 0.0
            val weightG = editWeight.text.toString().toDoubleOrNull() ?: 0.0

            val totalYieldKg = (count * fruits * weightG) / 1000.0
            val yieldPerM2 = totalYieldKg / area
            val yieldPerHa = yieldPerM2 * 10.0 // kg/ha

            view.findViewById<TextView>(R.id.resTotalYield).text = "%.2f kg".format(totalYieldKg)
            view.findViewById<TextView>(R.id.resYieldRange).text = "Range (±15%%): %.2f - %.2f kg".format(totalYieldKg * 0.85, totalYieldKg * 1.15)
            view.findViewById<TextView>(R.id.resYieldPerArea).text = "Yield: %.2f kg/m² (~%.1f kg/ha)".format(yieldPerM2, yieldPerHa)
            cardResults.visibility = View.VISIBLE

            viewModel.insertYieldEstimate(YieldEstimate(
                crop = crop, area = area, plantCount = count,
                fruitsPerPlant = fruits, avgFruitWeight = weightG,
                adjustedYield = totalYieldKg, rangeLow = totalYieldKg * 0.85, rangeHigh = totalYieldKg * 1.15
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
