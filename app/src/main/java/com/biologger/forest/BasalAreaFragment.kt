package com.biologger.forest
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.BasalAreaPlot
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlin.math.*

class BasalAreaFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()
    private val treeList = mutableListOf<TreeRow>()
    private lateinit var adapter: TreeRowAdapter

    data class TreeRow(var species: String = "", var dbh: Double = 0.0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_basal_area, container, false)

        val editPlotId = view.findViewById<TextInputEditText>(R.id.editPlotId)
        val spinnerShape = view.findViewById<AutoCompleteTextView>(R.id.spinnerShape)
        val editPlotSize = view.findViewById<TextInputEditText>(R.id.editPlotSize)
        val spinnerUnit = view.findViewById<AutoCompleteTextView>(R.id.spinnerUnit)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerTrees)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddTree)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalculateSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        spinnerShape.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Circular", "Square", "Rectangular")))
        spinnerUnit.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("m²", "hectares", "radius (m)")))

        adapter = TreeRowAdapter(treeList)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        btnAdd.setOnClickListener {
            treeList.add(TreeRow())
            adapter.notifyItemInserted(treeList.size - 1)
        }

        btnCalc.setOnClickListener {
            val plotSize = editPlotSize.text.toString().toDoubleOrNull() ?: 0.0
            val unit = spinnerUnit.text.toString()
            val areaM2 = when (unit) {
                "hectares" -> plotSize * 10000.0
                "radius (m)" -> PI * plotSize.pow(2)
                else -> plotSize
            }
            if (areaM2 <= 0 || treeList.isEmpty()) return@setOnClickListener

            var totalBA = 0.0
            var sumDbhSq = 0.0
            treeList.forEach {
                totalBA += PI * (it.dbh / 200.0).pow(2)
                sumDbhSq += it.dbh.pow(2)
            }

            val expansion = 10000.0 / areaM2
            view.findViewById<TextView>(R.id.resPlotBa).text = "%.4f m²".format(totalBA)
            view.findViewById<TextView>(R.id.resBaHa).text = "%.2f m²/ha".format(totalBA * expansion)
            view.findViewById<TextView>(R.id.resTreesHa).text = "%.1f N/ha".format(treeList.size * expansion)
            view.findViewById<TextView>(R.id.resQmd).text = "%.2f cm".format(sqrt(sumDbhSq / treeList.size))
            cardResults.visibility = View.VISIBLE

            viewModel.insertBasalAreaPlot(BasalAreaPlot(
                plotId = editPlotId.text.toString(),
                plotShape = spinnerShape.text.toString(),
                plotSize = plotSize,
                plotSizeUnit = unit,
                treeDbhListJson = Gson().toJson(treeList),
                totalBasalArea = totalBA,
                basalAreaPerHa = totalBA * expansion,
                treesPerHa = treeList.size * expansion,
                meanDbh = treeList.map { it.dbh }.average(),
                qmd = sqrt(sumDbhSq / treeList.size),
                latitude = null, longitude = null
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

    class TreeRowAdapter(private val list: MutableList<TreeRow>) : RecyclerView.Adapter<TreeRowAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val species: TextInputEditText = v.findViewById(R.id.editRowSpecies)
            val dbh: TextInputEditText = v.findViewById(R.id.editRowDbh)
            val remove: ImageButton = v.findViewById(R.id.btnRemoveRow)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_tree_dbh_row, parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.species.setText(item.species)
            holder.dbh.setText(if (item.dbh > 0) item.dbh.toString() else "")
            holder.species.setOnFocusChangeListener { _, h -> if (!h) item.species = holder.species.text.toString() }
            holder.dbh.setOnFocusChangeListener { _, h -> if (!h) item.dbh = holder.dbh.text.toString().toDoubleOrNull() ?: 0.0 }
            holder.remove.setOnClickListener { list.removeAt(holder.adapterPosition); notifyItemRemoved(holder.adapterPosition) }
        }
        override fun getItemCount() = list.size
    }
}
