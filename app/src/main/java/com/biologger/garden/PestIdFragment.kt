package com.biologger.garden
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class PestIdFragment : Fragment() {

    private lateinit var allPests: List<PestInfo>
    private val displayedPests = mutableListOf<PestInfo>()
    private lateinit var adapter: PestAdapter

    data class PestInfo(
        val name: String,
        val scientific: String,
        val crops: String,
        val symptoms: String,
        val damage: String,
        val management: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pest_id, container, false)
        val editSearch = view.findViewById<TextInputEditText>(R.id.editPestSearch)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerPests)

        val reader = InputStreamReader(requireContext().assets.open("pests.json"))
        allPests = Gson().fromJson(reader, object : TypeToken<List<PestInfo>>() {}.type)
        displayedPests.addAll(allPests)

        adapter = PestAdapter(displayedPests)
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
        displayedPests.clear()
        if (query.isEmpty()) {
            displayedPests.addAll(allPests)
        } else {
            val q = query.lowercase()
            allPests.forEach { if (it.name.lowercase().contains(q) || it.crops.lowercase().contains(q) || it.symptoms.lowercase().contains(q)) displayedPests.add(it) }
        }
        adapter.notifyDataSetChanged()
    }

    class PestAdapter(private val list: List<PestInfo>) : RecyclerView.Adapter<PestAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.textPestName)
            val sci: TextView = v.findViewById(R.id.textPestSci)
            val crops: TextView = v.findViewById(R.id.textPestCrops)
            val sym: TextView = v.findViewById(R.id.textPestSymptoms)
            val mgmt: TextView = v.findViewById(R.id.textPestMgmt)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_pest_info, parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.name.text = item.name
            holder.sci.text = item.scientific
            holder.crops.text = "Crops: " + item.crops
            holder.sym.text = "Symptoms: " + item.symptoms
            holder.mgmt.text = "Management: " + item.management
        }
        override fun getItemCount() = list.size
    }
}
