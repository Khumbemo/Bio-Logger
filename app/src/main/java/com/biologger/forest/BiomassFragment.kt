package com.biologger.forest

import android.content.Context
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
import com.biologger.data.BiomassResult
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.pow

class BiomassFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_biomass, container, false)

        val editTreeId = view.findViewById<TextInputEditText>(R.id.editTreeId)
        val editDbh = view.findViewById<TextInputEditText>(R.id.editDbh)
        val editHeight = view.findViewById<TextInputEditText>(R.id.editHeight)
        val editWoodDensity = view.findViewById<TextInputEditText>(R.id.editWoodDensity)
        val spinnerEquation = view.findViewById<AutoCompleteTextView>(R.id.spinnerEquation)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalculateSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        spinnerEquation.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Chave et al. (2014) Moist", "Chave et al. (2014) Dry", "Brown (1997)")))

        btnCalc.setOnClickListener {
            val dbh = editDbh.text.toString().toDoubleOrNull() ?: 0.0
            val height = editHeight.text.toString().toDoubleOrNull() ?: 0.0
            val rho = editWoodDensity.text.toString().toDoubleOrNull() ?: 0.6
            val eq = spinnerEquation.text.toString()

            val agbKg: Double = when (eq) {
                "Chave et al. (2014) Moist" -> 0.0673 * (rho * dbh.pow(2) * height).pow(0.976)
                "Chave et al. (2014) Dry" -> 0.0559 * (rho * dbh.pow(2) * height).pow(0.914)
                else -> 34.4703 - 8.0671 * dbh + 0.6589 * dbh.pow(2)
            }

            view.findViewById<TextView>(R.id.resAgbKg).text = "%.2f kg".format(agbKg)
            view.findViewById<TextView>(R.id.resAgbMg).text = "%.4f Mg".format(agbKg / 1000.0)
            view.findViewById<TextView>(R.id.resCarbon).text = "%.2f kg C".format(agbKg * 0.47)
            view.findViewById<TextView>(R.id.resCo2).text = "%.2f kg CO₂e".format(agbKg * 0.47 * 3.67)
            cardResults.visibility = View.VISIBLE

            viewModel.insertBiomass(BiomassResult(
                treeId = editTreeId.text.toString(), species = "", dbh = dbh, height = height,
                woodDensity = rho, equationName = eq, carbonFraction = 0.47, co2Factor = 3.67,
                agbKg = agbKg, agbMg = agbKg / 1000.0, carbonStockKg = agbKg * 0.47, co2EqKg = agbKg * 0.47 * 3.67,
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
