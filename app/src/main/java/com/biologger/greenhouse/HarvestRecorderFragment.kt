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
import com.biologger.data.HarvestRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class HarvestRecorderFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_harvest_recorder, container, false)
        val editArea = view.findViewById<TextInputEditText>(R.id.editArea)
        val editWeight = view.findViewById<TextInputEditText>(R.id.editWeight)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnSave.setOnClickListener {
            val area = editArea.text.toString().toDoubleOrNull() ?: 1.0
            val weight = editWeight.text.toString().toDoubleOrNull() ?: 0.0
            val yieldGm2 = weight / area
            val yieldKgha = yieldGm2 * 10.0

            view.findViewById<TextView>(R.id.resYield).text = "Yield: %.1f kg/ha".format(yieldKgha)
            cardResults.visibility = View.VISIBLE

            viewModel.insertHarvestRecord(HarvestRecord(
                plotId = "", crop = view.findViewById<TextInputEditText>(R.id.editCrop).text.toString(),
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                freshWeight = weight, dryWeight = null, moisturePercent = null,
                yieldGm2 = yieldGm2, yieldKgha = yieldKgha, harvestIndex = null, qualityGrade = "A"
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
