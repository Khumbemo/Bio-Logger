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
import com.biologger.data.SoilAmendmentPlan
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.abs

class SoilAmendmentFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_soil_amendment, container, false)
        val editCurrent = view.findViewById<TextInputEditText>(R.id.editCurrentPh)
        val editTarget = view.findViewById<TextInputEditText>(R.id.editTargetPh)
        val editArea = view.findViewById<TextInputEditText>(R.id.editArea)
        val spinnerSoil = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.spinnerSoilType)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalc)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        val soilTypes = arrayOf("Sand", "Loam", "Clay")
        spinnerSoil.setSimpleItems(soilTypes)

        btnCalc.setOnClickListener {
            val curr = editCurrent.text.toString().toDoubleOrNull() ?: 7.0
            val target = editTarget.text.toString().toDoubleOrNull() ?: 6.5
            val area = editArea.text.toString().toDoubleOrNull() ?: 1.0
            val soil = spinnerSoil.text.toString()

            // Buffer Capacity Factor (kg/m2 per pH unit change)
            val factor = when(soil) {
                "Sand" -> 0.05
                "Clay" -> 0.15
                else -> 0.10 // Loam default
            }

            val diff = abs(target - curr)
            val amount = diff * area * factor

            val label = if (target < curr) "Sulfur needed: %.2f kg".format(amount) else "Lime needed: %.2f kg".format(amount)
            view.findViewById<TextView>(R.id.resAmendment).text = label
            cardResults.visibility = View.VISIBLE

            viewModel.insertSoilAmendmentPlan(SoilAmendmentPlan(
                currentPh = curr, targetPh = target, soilType = soil, area = area,
                amendmentType = if (target < curr) "Sulfur" else "Lime", calculatedAmount = amount
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
