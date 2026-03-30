package com.biologger.garden

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class PlantWikiFragment : Fragment() {

    private lateinit var allCrops: List<CropWiki>
    private val displayedCrops = mutableListOf<CropWiki>()
    private lateinit var adapter: WikiAdapter

    data class CropWiki(
        val name: String,
        val scientific: String,
        val family: String,
        val season: String,
        val maturity_days: Int,
        val ph_range: String,
        val local_names: Map<String, String>?,
        val tips: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_wiki, container, false)
        val editSearch = view.findViewById<TextInputEditText>(R.id.editSearch)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerWiki)

        // Load JSON
        val inputStream = requireContext().assets.open("crops.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<CropWiki>>() {}.type
        allCrops = Gson().fromJson(reader, type)
        displayedCrops.addAll(allCrops)

        adapter = WikiAdapter(displayedCrops)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    private fun filter(query: String) {
        displayedCrops.clear()
        if (query.isEmpty()) {
            displayedCrops.addAll(allCrops)
        } else {
            val lowerQuery = query.lowercase()
            allCrops.forEach { crop ->
                if (crop.name.lowercase().contains(lowerQuery) ||
                    crop.scientific.lowercase().contains(lowerQuery) ||
                    crop.local_names?.values?.any { it.lowercase().contains(lowerQuery) } == true) {
                    displayedCrops.add(crop)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    class WikiAdapter(private val list: List<CropWiki>) : RecyclerView.Adapter<WikiAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.textCropName)
            val sci: TextView = v.findViewById(R.id.textScientific)
            val mat: TextView = v.findViewById(R.id.textMaturity)
            val ph: TextView = v.findViewById(R.id.textPh)
            val tips: TextView = v.findViewById(R.id.textTips)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_plant_wiki, parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            val localStr = if (item.local_names != null && item.local_names.isNotEmpty()) {
                " (" + item.local_names.values.first() + ")"
            } else ""
            holder.name.text = item.name + localStr
            holder.sci.text = item.scientific
            holder.mat.text = "Maturity: ${item.maturity_days} days (${item.season})"
            holder.ph.text = "pH: ${item.ph_range}"
            holder.tips.text = item.tips
        }
        override fun getItemCount() = list.size

        // Helper extension
        private fun <K, V> Map<K, V>?.isNull_or_empty() = this == null || this.isEmpty()
    }
}
