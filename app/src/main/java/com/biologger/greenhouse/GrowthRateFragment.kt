package com.biologger.greenhouse

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.GrowthRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlin.math.ln

class GrowthRateFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    private val readings = mutableListOf<GrowthReading>()
    private lateinit var adapter: GrowthReadingAdapter

    data class GrowthReading(var days: Int = 0, var height: Double = 0.0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_growth_rate, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerGrowth)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAdd)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        adapter = GrowthReadingAdapter(readings)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        btnAdd.setOnClickListener {
            readings.add(GrowthReading())
            adapter.notifyItemInserted(readings.size - 1)
        }

        btnSave.setOnClickListener {
            if (readings.size < 2) {
                Toast.makeText(context, "At least 2 measurements required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val r1 = readings.first()
            val r2 = readings.last()

            // Relative Growth Rate (RGR) = (ln H2 - ln H1) / (T2 - T1)
            val rgr = (ln(r2.height) - ln(r1.height)) / (r2.days - r1.days)
            val agr = (r2.height - r1.height) / (r2.days - r1.days) // Absolute Growth Rate

            Toast.makeText(context, "RGR: %.4f cm/cm/day".format(rgr), Toast.LENGTH_LONG).show()

            viewModel.insertGrowthRecord(GrowthRecord(
                plantId = view.findViewById<TextInputEditText>(R.id.editPlantId).text.toString(),
                measurementsJson = Gson().toJson(readings),
                indicesJson = "{\"rgr\": $rgr, \"agr\": $agr}"
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

    class GrowthReadingAdapter(private val list: List<GrowthReading>) : RecyclerView.Adapter<GrowthReadingAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val days: TextInputEditText = v.findViewById(R.id.editDays)
            val height: TextInputEditText = v.findViewById(R.id.editHeight)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_growth_reading, parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.days.setText(if (item.days > 0) item.days.toString() else "")
            holder.height.setText(if (item.height > 0) item.height.toString() else "")

            holder.days.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) item.days = holder.days.text.toString().toIntOrNull() ?: 0
            }
            holder.height.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) item.height = holder.height.text.toString().toDoubleOrNull() ?: 0.0
            }
        }
        override fun getItemCount() = list.size
    }
}
