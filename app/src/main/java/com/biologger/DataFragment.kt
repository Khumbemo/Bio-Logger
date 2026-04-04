package com.biologger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class DataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data, container, false)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvDataCategories)
        rv.layoutManager = LinearLayoutManager(requireContext())
        
        val categories = listOf(
            DataCategory("Forestry Measurements", "Tree DBH, height, and health records"),
            DataCategory("Agro-Climatic Logs", "Temperature, humidity, and germination trials"),
            DataCategory("Horticulture Data", "Yield records and soil amendments"),
            DataCategory("Field Notes", "Observations and digital herbarium")
        )
        
        rv.adapter = DataAdapter(categories)
        
        return view
    }

    data class DataCategory(val title: String, val subtitle: String)

    class DataAdapter(private val items: List<DataCategory>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvTitle)
            val subtitle: TextView = view.findViewById(R.id.tvSubtitle)
            val card: MaterialCardView = view.findViewById(R.id.dataCard)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_data_category, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.subtitle.text = item.subtitle
            holder.card.setOnClickListener {
                // Future: Navigate to export or detailed list
            }
        }

        override fun getItemCount() = items.size
    }
}
