package com.biologger.garden

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.CompostBatch
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class CompostTrackerFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_compost_tracker, container, false)
        val editBatch = view.findViewById<TextInputEditText>(R.id.editBatchId)
        val editTemp = view.findViewById<TextInputEditText>(R.id.editTemp)
        val btnLog = view.findViewById<MaterialButton>(R.id.btnLog)
        val cardStatus = view.findViewById<MaterialCardView>(R.id.cardStatus)
        val textStage = view.findViewById<TextView>(R.id.textCompostStage)
        val textAdvice = view.findViewById<TextView>(R.id.textCompostAdvice)
        val progress = view.findViewById<ProgressBar>(R.id.progressCompost)

        btnLog.setOnClickListener {
            val temp = editTemp.text.toString().toDoubleOrNull() ?: 25.0

            val (stage, advice, perc) = when {
                temp < 20.0 -> Triple("Initial", "Slow activity. Add green materials.", 10)
                temp < 40.0 -> Triple("Mesophilic", "Good decomposition. Keep moist.", 40)
                temp < 70.0 -> Triple("Thermophilic", "Rapid decomposition. Pathogens dying.", 80)
                else -> Triple("Overheating", "Too hot! Turn the pile immediately.", 90)
            }

            textStage.text = "Stage: $stage"
            textAdvice.text = advice
            progress.progress = perc
            cardStatus.visibility = View.VISIBLE

            viewModel.insertCompostBatch(CompostBatch(
                batchId = editBatch.text.toString(),
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                materialsJson = "{}",
                tempLogJson = "{\"temp\": $temp}",
                stage = stage,
                notes = "Current Temp: $temp°C"
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
