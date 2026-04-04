package com.biologger.garden

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.WateringSchedule
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class WateringScheduleFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_watering_schedule, container, false)
        
        val editPlantName = view.findViewById<TextInputEditText>(R.id.editPlantName)
        val editFrequency = view.findViewById<TextInputEditText>(R.id.editFrequency)
        val editAmount = view.findViewById<TextInputEditText>(R.id.editAmount)
        val editNotes = view.findViewById<TextInputEditText>(R.id.editNotes)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveSchedule)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerWatering)

        recyclerView.layoutManager = LinearLayoutManager(context)

        btnSave.setOnClickListener {
            val name = editPlantName.text.toString()
            val freq = editFrequency.text.toString().toIntOrNull() ?: 1
            val amount = editAmount.text.toString().toDoubleOrNull() ?: 0.0
            val notes = editNotes.text.toString()

            if (name.isNotEmpty()) {
                val schedule = WateringSchedule(
                    crop = name,
                    stage = "General Investigation",
                    netDailyNeed = if (freq > 0) amount / freq else amount,
                    interval = freq,
                    volume = amount,
                    scheduleJson = notes
                )
                viewModel.insertWateringSchedule(schedule)
                Toast.makeText(context, "Scientific schedule saved", Toast.LENGTH_SHORT).show()
                
                // Reset fields
                editPlantName.text = null
                editFrequency.text = null
                editAmount.text = null
                editNotes.text = null
            } else {
                Toast.makeText(context, "Precision Error: Crop name required", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
        
        view.setOnClickListener {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        return view
    }
}
