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
import com.biologger.data.PestDiseaseRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class PestDiseaseLogFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pest_disease_log, container, false)
        val editInc = view.findViewById<TextInputEditText>(R.id.editIncidence)
        val sliderSev = view.findViewById<Slider>(R.id.sliderSeverity)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardRes = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnSave.setOnClickListener {
            val inc = editInc.text.toString().toDoubleOrNull() ?: 0.0
            val sev = sliderSev.value.toDouble()
            val dsi = (inc * sev) / 5.0

            view.findViewById<TextView>(R.id.resDsi).text = "DSI: %.1f%%".format(dsi)
            view.findViewById<TextView>(R.id.resRisk).text = "Risk: Moderate"
            cardRes.visibility = View.VISIBLE

            viewModel.insertPestDiseaseRecord(PestDiseaseRecord(
                crop = "", date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                problemType = "Pest", name = view.findViewById<TextInputEditText>(R.id.editName).text.toString(),
                severity = sev.toInt(), incidencePercent = inc, areaAffectedPercent = 0.0, photoPaths = null,
                actionTaken = "", followUpDate = null, dsi = dsi, riskLevel = "Moderate", latitude = null, longitude = null
            ))
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.setPadding(0, 0, 0, imeHeight)
            insets
        }
        view.setOnClickListener {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
        return view
    }
}