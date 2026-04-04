package com.biologger.forest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.TreeMeasurement
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.pow

class TreeMeasurementFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tree_measurement, container, false)

        val editPlotId = view.findViewById<TextInputEditText>(R.id.editPlotId)
        val editTreeId = view.findViewById<TextInputEditText>(R.id.editTreeId)
        val editSpecies = view.findViewById<TextInputEditText>(R.id.editSpecies)
        val editDbh = view.findViewById<TextInputEditText>(R.id.editDbh)
        val editHeight = view.findViewById<TextInputEditText>(R.id.editHeight)
        val editCrownNS = view.findViewById<TextInputEditText>(R.id.editCrownNS)
        val editCrownEW = view.findViewById<TextInputEditText>(R.id.editCrownEW)
        val editDate = view.findViewById<TextInputEditText>(R.id.editDate)
        val editObserver = view.findViewById<TextInputEditText>(R.id.editObserver)
        val editNotes = view.findViewById<TextInputEditText>(R.id.editNotes)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveMeasurement)
        val btnClear = view.findViewById<MaterialButton>(R.id.btnClearFields)

        editDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        editDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.show(parentFragmentManager, "DATE")
            picker.addOnPositiveButtonClickListener {
                editDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it)))
            }
        }

        btnSave.setOnClickListener {
            val dbh = editDbh.text.toString().toDoubleOrNull() ?: 0.0
            val height = editHeight.text.toString().toDoubleOrNull() ?: 1.0
            val cNS = editCrownNS.text.toString().toDoubleOrNull() ?: 0.0
            val cEW = editCrownEW.text.toString().toDoubleOrNull() ?: 0.0

            val ba = PI * (dbh / 200.0).pow(2)
            val meanCrown = (cNS + cEW) / 2.0

            val measurement = TreeMeasurement(
                plotId = editPlotId.text.toString(),
                treeId = editTreeId.text.toString(),
                speciesName = editSpecies.text.toString(),
                dbh = dbh,
                height = height,
                crownNS = cNS,
                crownEW = cEW,
                date = editDate.text.toString(),
                observer = editObserver.text.toString(),
                notes = editNotes.text.toString(),
                basalArea = ba,
                crossSectionalArea = PI * (dbh/2.0).pow(2),
                meanCrownDiameter = meanCrown,
                crownArea = PI * (meanCrown/2.0).pow(2),
                crownRatio = meanCrown / height,
                latitude = null, longitude = null
            )
            viewModel.insertTreeMeasurement(measurement)
            Snackbar.make(view, "Saved successfully", Snackbar.LENGTH_SHORT).show()
        }

        btnClear.setOnClickListener {
            listOf(editPlotId, editTreeId, editSpecies, editDbh, editHeight, editCrownNS, editCrownEW, editNotes).forEach { it.text = null }
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
