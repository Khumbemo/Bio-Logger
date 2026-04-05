package com.biologger.forest
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
import com.biologger.data.LitterfallRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class LitterfallFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_litterfall, container, false)

        val editPlotId = view.findViewById<TextInputEditText>(R.id.editPlotId)
        val editTrapArea = view.findViewById<TextInputEditText>(R.id.editTrapArea)
        val editInterval = view.findViewById<TextInputEditText>(R.id.editInterval)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnSave.setOnClickListener {
            val area = editTrapArea.text.toString().toDoubleOrNull() ?: 0.25
            val interval = editInterval.text.toString().toIntOrNull() ?: 1
            val fresh = (view.findViewById<TextInputEditText>(R.id.massLeaves).text.toString().toDoubleOrNull() ?: 0.0) +
                        (view.findViewById<TextInputEditText>(R.id.massTwigs).text.toString().toDoubleOrNull() ?: 0.0) +
                        (view.findViewById<TextInputEditText>(R.id.massBark).text.toString().toDoubleOrNull() ?: 0.0)
            val dry = fresh * 0.85
            val rate = dry / (area * interval)

            view.findViewById<TextView>(R.id.resRate).text = "%.3f".format(rate)
            view.findViewById<TextView>(R.id.resFluxMg).text = "%.3f".format(rate * 3.65)
            cardResults.visibility = View.VISIBLE

            viewModel.insertLitterfall(LitterfallRecord(
                plotId = editPlotId.text.toString(),
                trapId = view.findViewById<TextInputEditText>(R.id.editTrapId).text.toString(),
                trapArea = area,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                intervalDays = interval, fractionMassesJson = "", moistureCorrection = 0.85,
                totalDryMass = dry, rate = rate, annualFluxG = rate * 365.0, annualFluxMg = rate * 3.65,
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
