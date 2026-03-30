package com.biologger.greenhouse

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.FertilizerPlan
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class FertilizerCalculatorFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_fertilizer_calculator, container, false)
        val editArea = view.findViewById<TextInputEditText>(R.id.editArea)
        val editN = view.findViewById<TextInputEditText>(R.id.editN)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalc)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnCalc.setOnClickListener {
            val area = editArea.text.toString().toDoubleOrNull() ?: 0.0
            val nTarget = editN.text.toString().toDoubleOrNull() ?: 0.0
            if (area <= 0) return@setOnClickListener

            val areaHa = area / 10000.0
            val reqN = nTarget * areaHa
            val ureaAmount = reqN / 0.46 // Urea is 46% N

            view.findViewById<TextView>(R.id.resDose).text = "Urea Needed: %.2f kg".format(ureaAmount)
            cardResults.visibility = View.VISIBLE

            viewModel.insertFertilizerPlan(FertilizerPlan(
                crop = "", stage = "", area = area, areaUnit = "m²",
                targetN = nTarget, targetP = 0.0, targetK = 0.0, productsJson = "",
                method = "Broadcasting", splits = 1, calculatedDosesJson = ureaAmount.toString()
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