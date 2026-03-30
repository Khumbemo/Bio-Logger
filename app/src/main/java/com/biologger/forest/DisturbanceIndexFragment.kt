package com.biologger.forest

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
import com.biologger.data.DisturbanceRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class DisturbanceIndexFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()
    private val indicators = arrayOf("Fire", "Logging", "Invasive", "Erosion", "Gap", "Grazing", "Human", "Flood")
    private val sliders = mutableMapOf<String, Slider>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_disturbance_index, container, false)

        val layout = view.findViewById<ViewGroup>(R.id.layoutIndicators)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        indicators.forEach { label ->
            val slider = Slider(requireContext()).apply { valueFrom = 0f; valueTo = 3f; stepSize = 1f }
            layout.addView(TextView(context).apply { text = label })
            layout.addView(slider)
            sliders[label] = slider
        }

        btnSave.setOnClickListener {
            var total = 0f
            sliders.values.forEach { total += it.value }
            val di = (total / (indicators.size * 3)) * 100.0
            val cat = if (di <= 20) "Minimal" else if (di <= 50) "Moderate" else "Heavily disturbed"

            view.findViewById<TextView>(R.id.resDiPercent).text = "DI: %.1f%%".format(di)
            view.findViewById<TextView>(R.id.resCategory).text = cat
            cardResults.visibility = View.VISIBLE

            viewModel.insertDisturbanceRecord(DisturbanceRecord(
                plotId = view.findViewById<TextView>(R.id.editPlotId).text.toString(),
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                indicatorScoresJson = Gson().toJson(sliders.mapValues { it.value.value }), diPercent = di, category = cat,
                photoPath = null, latitude = null, longitude = null, notes = null
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