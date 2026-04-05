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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.TransectStudy
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class TransectStudiesFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()
    private val encounters = mutableListOf<Encounter>()
    private lateinit var adapter: EncounterAdapter

    data class Encounter(var species: String = "", var count: Int = 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transect_studies, container, false)

        val editLength = view.findViewById<TextInputEditText>(R.id.editLength)
        val editWidth = view.findViewById<TextInputEditText>(R.id.editWidth)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerEncounters)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddEncounter)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        adapter = EncounterAdapter(encounters)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        btnAdd.setOnClickListener {
            encounters.add(Encounter())
            adapter.notifyItemInserted(encounters.size - 1)
        }

        btnSave.setOnClickListener {
            val area = (editLength.text.toString().toDoubleOrNull() ?: 1.0) * (editWidth.text.toString().toDoubleOrNull() ?: 1.0)
            val summary = encounters.joinToString("\n") { "${it.species}: %.1f /ha".format((it.count / area) * 10000.0) }
            view.findViewById<TextView>(R.id.resSummary).text = summary
            cardResults.visibility = View.VISIBLE

            viewModel.insertTransectStudy(TransectStudy(
                transectId = view.findViewById<TextInputEditText>(R.id.editTransectId).text.toString(),
                type = "Belt", length = editLength.text.toString().toDoubleOrNull() ?: 0.0,
                width = editWidth.text.toString().toDoubleOrNull() ?: 0.0,
                startLat = null, startLng = null, endLat = null, endLng = null,
                surveyType = "Vegetation", encounterDataJson = Gson().toJson(encounters),
                totalArea = area, notes = null
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

    class EncounterAdapter(private val list: MutableList<Encounter>) : RecyclerView.Adapter<EncounterAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextInputEditText = v.findViewById(R.id.editSpecName)
            val count: TextInputEditText = v.findViewById(R.id.editSpecCount)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_quadrat_species, parent, false)
            v.findViewById<View>(R.id.editSpecCover).visibility = View.GONE
            return VH(v)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.name.setText(item.species)
            holder.count.setText(if (item.count > 0) item.count.toString() else "")
            holder.name.setOnFocusChangeListener { _, h -> if (!h) item.species = holder.name.text.toString() }
            holder.count.setOnFocusChangeListener { _, h -> if (!h) item.count = holder.count.text.toString().toIntOrNull() ?: 0 }

            val removeBtn = holder.itemView.findViewById<android.widget.ImageButton>(R.id.btnRemoveRow)
            removeBtn?.visibility = View.VISIBLE
            removeBtn?.setOnClickListener {
                val currentPos = holder.bindingAdapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    list.removeAt(currentPos)
                    notifyItemRemoved(currentPos)
                }
            }
        }
        override fun getItemCount() = list.size
    }
}
