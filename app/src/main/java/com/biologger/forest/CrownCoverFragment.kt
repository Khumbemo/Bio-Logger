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
import com.biologger.data.CrownCoverRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class CrownCoverFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crown_cover, container, false)

        val spinnerMethod = view.findViewById<AutoCompleteTextView>(R.id.spinnerMethod)
        val editTotalPoints = view.findViewById<EditText>(R.id.editTotalPoints)
        val editHitPoints = view.findViewById<EditText>(R.id.editHitPoints)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        spinnerMethod.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Point Intercept", "Spherical Densiometer")))
        spinnerMethod.setOnItemClickListener { _, _, position, _ ->
            view.findViewById<View>(R.id.layoutPointIntercept).visibility = if (position == 0) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.layoutDensiometer).visibility = if (position == 1) View.VISIBLE else View.GONE
        }

        btnSave.setOnClickListener {
            val method = spinnerMethod.text.toString()
            var cover = 0.0
            if (method == "Point Intercept") {
                val total = editTotalPoints.text.toString().toDoubleOrNull() ?: 1.0
                cover = ((editHitPoints.text.toString().toDoubleOrNull() ?: 0.0) / total) * 100.0
            } else {
                cover = ((view.findViewById<EditText>(R.id.editR1).text.toString().toDoubleOrNull() ?: 0.0) +
                         (view.findViewById<EditText>(R.id.editR2).text.toString().toDoubleOrNull() ?: 0.0) +
                         (view.findViewById<EditText>(R.id.editR3).text.toString().toDoubleOrNull() ?: 0.0) +
                         (view.findViewById<EditText>(R.id.editR4).text.toString().toDoubleOrNull() ?: 0.0)) / 4.0 * 1.04
            }

            view.findViewById<TextView>(R.id.resCoverPercent).text = "Cover: %.1f%%".format(cover)
            cardResults.visibility = View.VISIBLE

            viewModel.insertCrownCover(CrownCoverRecord(
                plotId = view.findViewById<EditText>(R.id.editPlotId).text.toString(), method = method,
                totalPoints = editTotalPoints.text.toString().toIntOrNull(), hitPoints = editHitPoints.text.toString().toIntOrNull(),
                densiometerReadings = null, visualCover = null, layer = "Upper", coverPercent = cover,
                opennessPercent = 100.0 - cover, lightCategory = "", latitude = null, longitude = null, notes = null
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
