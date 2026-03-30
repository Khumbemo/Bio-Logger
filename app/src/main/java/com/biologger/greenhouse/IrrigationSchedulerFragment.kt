package com.biologger.greenhouse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.IrrigationPlan
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class IrrigationSchedulerFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_irrigation_scheduler, container, false)
        val editEt0 = view.findViewById<TextInputEditText>(R.id.editEt0)
        val spinnerStage = view.findViewById<AutoCompleteTextView>(R.id.spinnerStage)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardRes = view.findViewById<MaterialCardView>(R.id.cardResults)

        spinnerStage.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Establishment", "Vegetative", "Reproductive", "Maturity")))

        btnSave.setOnClickListener {
            val et0 = editEt0.text.toString().toDoubleOrNull() ?: 0.0
            val kc = when(spinnerStage.text.toString()) {
                "Establishment" -> 0.5
                "Vegetative" -> 0.85
                "Reproductive" -> 1.15
                "Maturity" -> 0.75
                else -> 0.85
            }
            val etc = et0 * kc

            view.findViewById<TextView>(R.id.resIrrigation).text = "ETc: %.2f mm/day".format(etc)
            view.findViewById<TextView>(R.id.resInterval).text = "Recommended: Every 3 days"
            cardRes.visibility = View.VISIBLE

            viewModel.insertIrrigationPlan(IrrigationPlan(
                crop = "", stage = spinnerStage.text.toString(), kc = kc, et0 = et0, rainfall = 0.0, soilType = "Loam",
                rootDepth = 30.0, efficiency = 0.8, netRequirement = etc, interval = 3,
                volumePerEvent = etc * 3, scheduleJson = ""
            ))
            Toast.makeText(context, "Irrigation plan saved", Toast.LENGTH_SHORT).show()
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
