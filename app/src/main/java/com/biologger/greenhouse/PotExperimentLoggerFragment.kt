package com.biologger.greenhouse

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.PotObservation
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class PotExperimentLoggerFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    private val logItems = mutableListOf<PotLogItem>()
    private lateinit var adapter: PotLogAdapter

    data class PotLogItem(val potId: String, var height: Double = 0.0, var leafCount: Int = 0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pot_experiment_logger, container, false)
        val spinnerExp = view.findViewById<AutoCompleteTextView>(R.id.spinnerExp)
        val editRound = view.findViewById<TextInputEditText>(R.id.editRound)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerLog)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        // Load active experiments
        viewModel.allPotExperiments.observe(viewLifecycleOwner) { exps ->
            val titles = exps.map { it.title }.toTypedArray()
            spinnerExp.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, titles))
            spinnerExp.setOnItemClickListener { _, _, position, _ ->
                val selected = exps[position]
                setupPots(selected.allocationJson)
            }
        }

        adapter = PotLogAdapter(logItems)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        btnSave.setOnClickListener {
            val expTitle = spinnerExp.text.toString()
            if (expTitle.isEmpty()) {
                Toast.makeText(context, "Select an experiment first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.insertPotObservation(PotObservation(
                experimentId = expTitle,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                round = editRound.text.toString().toIntOrNull() ?: 1,
                readingsJson = Gson().toJson(logItems),
                summariesJson = "{}"
            ))
            Toast.makeText(context, "Observations saved", Toast.LENGTH_SHORT).show()
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

    private fun setupPots(allocationJson: String) {
        logItems.clear()
        val pots = allocationJson.split(",").filter { it.isNotEmpty() }
        pots.forEach { logItems.add(PotLogItem(it.trim())) }
        adapter.notifyDataSetChanged()
    }

    class PotLogAdapter(private val list: List<PotLogItem>) : RecyclerView.Adapter<PotLogAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val id: TextView = v.findViewById(R.id.textPotId)
            val h: TextInputEditText = v.findViewById(R.id.editPotHeight)
            val l: TextInputEditText = v.findViewById(R.id.editPotLeaves)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_pot_log, parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.id.text = item.potId
            holder.h.setText(if (item.height > 0) item.height.toString() else "")
            holder.l.setText(if (item.leafCount > 0) item.leafCount.toString() else "")

            holder.h.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) item.height = holder.h.text.toString().toDoubleOrNull() ?: 0.0
            }
            holder.l.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) item.leafCount = holder.l.text.toString().toIntOrNull() ?: 0
            }
        }
        override fun getItemCount() = list.size
    }
}
