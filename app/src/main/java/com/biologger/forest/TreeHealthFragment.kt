package com.biologger.forest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.TreeHealthRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.*

class TreeHealthFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tree_health, container, false)

        val editTreeId = view.findViewById<EditText>(R.id.editTreeId)
        val radioGroupStatus = view.findViewById<RadioGroup>(R.id.radioHealthStatus)
        val sliderSeverity = view.findViewById<Slider>(R.id.sliderSeverity)
        val sliderDieback = view.findViewById<Slider>(R.id.sliderDieback)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnSave.setOnClickListener {
            val checkedId = radioGroupStatus.checkedRadioButtonId
            if (checkedId == -1) {
                Toast.makeText(requireContext(), "Select health status", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val status = view.findViewById<RadioButton>(checkedId).text.toString()
            val severity = sliderSeverity.value.toInt()
            val dieback = sliderDieback.value.toDouble()

            var score = when (status) { "Healthy" -> 100.0; "Stressed" -> 65.0; "Declining" -> 35.0; else -> 0.0 }
            score = maxOf(0.0, score - (severity - 1) * 8.0 - dieback * 0.3)

            val (cat, color) = when { score >= 80 -> "Good" to Color.GREEN; score >= 60 -> "Fair" to Color.YELLOW; score >= 40 -> "Poor" to Color.rgb(255, 165, 0); else -> "Critical" to Color.RED }

            val recommendation = when(cat) {
                "Good" -> "No immediate action. Monitor annually."
                "Fair" -> "Monitor for pest/disease progression. Consider soil check."
                "Poor" -> "Action required: Pruning or pest management may be needed."
                "Critical" -> "Severe hazard. Evaluation for removal or intensive care."
                else -> ""
            }

            view.findViewById<TextView>(R.id.resHealthScore).text = "Score: %.1f".format(score)
            view.findViewById<TextView>(R.id.resHealthCategory).text = cat
            view.findViewById<TextView>(R.id.resHealthCategory).setTextColor(color)
            view.findViewById<TextView>(R.id.resRecommendation).text = recommendation
            cardResults.visibility = View.VISIBLE

            viewModel.insertTreeHealth(TreeHealthRecord(
                treeId = editTreeId.text.toString(), species = "", plotId = "", date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                status = status, damageTypes = "", severity = severity, dieback = dieback, symptoms = "",
                photoPath = null, healthScore = score, healthCategory = cat, latitude = null, longitude = null, notes = null
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
