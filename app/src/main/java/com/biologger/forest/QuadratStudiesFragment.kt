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
import com.biologger.data.QuadratStudy
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlin.math.ln

class QuadratStudiesFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()
    private val speciesList = mutableListOf<SpeciesRow>()
    private lateinit var adapter: SpeciesAdapter

    data class SpeciesRow(var name: String = "", var count: Int = 0, var cover: Double = 0.0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quadrat_studies, container, false)

        val editPlotId = view.findViewById<TextInputEditText>(R.id.editPlotId)
        val editSize = view.findViewById<TextInputEditText>(R.id.editSize)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSpecies)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddSpecies)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalculate)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        adapter = SpeciesAdapter(speciesList)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        btnAdd.setOnClickListener {
            speciesList.add(SpeciesRow())
            adapter.notifyItemInserted(speciesList.size - 1)
        }

        btnCalc.setOnClickListener {
            var totalCount = 0
            speciesList.forEach { totalCount += it.count }
            if (totalCount == 0) return@setOnClickListener

            var shannon = 0.0
            var simpsonSum = 0.0
            speciesList.forEach {
                val p = it.count.toDouble() / totalCount
                if (p > 0) {
                    shannon -= p * ln(p)
                    simpsonSum += it.count * (it.count - 1)
                }
            }
            val simpsonD = if (totalCount > 1) 1.0 - (simpsonSum / (totalCount * (totalCount - 1))) else 0.0
            val evenness = if (speciesList.size > 1) shannon / ln(speciesList.size.toDouble()) else 0.0

            view.findViewById<TextView>(R.id.resRichness).text = speciesList.size.toString()
            view.findViewById<TextView>(R.id.resShannon).text = "%.3f".format(shannon)
            view.findViewById<TextView>(R.id.resSimpson).text = "%.3f".format(simpsonD)
            cardResults.visibility = View.VISIBLE

            viewModel.insertQuadratStudy(QuadratStudy(
                plotId = editPlotId.text.toString(),
                quadratId = view.findViewById<TextInputEditText>(R.id.editQuadratId).text.toString(),
                size = editSize.text.toString().toDoubleOrNull() ?: 1.0,
                layer = "Herb", speciesDataJson = Gson().toJson(speciesList), richness = speciesList.size,
                shannonH = shannon, evennessJ = evenness, simpsonD = simpsonD,
                dominantSpecies = speciesList.maxByOrNull { it.count }?.name ?: "",
                latitude = null, longitude = null, notes = null
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

    class SpeciesAdapter(private val list: MutableList<SpeciesRow>) : RecyclerView.Adapter<SpeciesAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextInputEditText = v.findViewById(R.id.editSpecName)
            val count: TextInputEditText = v.findViewById(R.id.editSpecCount)
            val remove: android.widget.ImageButton = v.findViewById(R.id.btnRemoveRow)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_quadrat_species, parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.name.setText(item.name)
            holder.count.setText(if (item.count > 0) item.count.toString() else "")
            holder.name.setOnFocusChangeListener { _, h -> if(!h) item.name = holder.name.text.toString() }
            holder.count.setOnFocusChangeListener { _, h -> if(!h) item.count = holder.count.text.toString().toIntOrNull() ?: 0 }
            holder.remove.setOnClickListener {
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
