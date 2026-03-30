package com.biologger.greenhouse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.PotExperiment
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class PotExperimentFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pot_experiment, container, false)
        val editT = view.findViewById<TextInputEditText>(R.id.editTreatments)
        val editR = view.findViewById<TextInputEditText>(R.id.editReplicates)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardRes = view.findViewById<MaterialCardView>(R.id.cardResults)
        val spinnerRandom = view.findViewById<AutoCompleteTextView>(R.id.spinnerRandom)

        spinnerRandom.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("CRD", "RCBD", "Split-plot")))

        val textTotalPots = view.findViewById<TextView>(R.id.resTotalPots)
        val textAllocation = view.findViewById<TextView>(R.id.resAllocation)
        val editTitle = view.findViewById<TextInputEditText>(R.id.editTitle)
        val editCrop = view.findViewById<TextInputEditText>(R.id.editCrop)

        btnSave.setOnClickListener {
            val t = editT.text.toString().toIntOrNull() ?: 0
            val r = editR.text.toString().toIntOrNull() ?: 0
            val total = t * r
            textTotalPots.text = "Total Pots: $total"

            // Randomisation logic
            val pots = mutableListOf<String>()
            for (i in 1..t) {
                for (j in 1..r) {
                    pots.add("T$i-R$j")
                }
            }
            pots.shuffle()
            textAllocation.text = "Allocation: " + pots.take(5).joinToString(", ") + "..."

            cardRes.visibility = View.VISIBLE

            viewModel.insertPotExperiment(PotExperiment(
                expId = "EXP-${System.currentTimeMillis()}",
                title = editTitle.text.toString(),
                crop = editCrop.text.toString(),
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                durationWeeks = 8, treatmentNamesJson = "", replicates = r, potSize = 0.0, medium = "",
                randomisation = spinnerRandom.text.toString(), wateringFreq = "", parametersJson = "", allocationJson = pots.joinToString(","), scheduleJson = "", status = "Active"
            ))
            Toast.makeText(context, "Experiment setup saved", Toast.LENGTH_SHORT).show()
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
