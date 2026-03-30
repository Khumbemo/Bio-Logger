package com.biologger.garden
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class PlanningCalendarFragment : Fragment() {

    private lateinit var allActivities: List<MonthActivities>
    private val displayedTasks = mutableListOf<ActivityTask>()
    private lateinit var adapter: TaskAdapter

    data class MonthActivities(val month: String, val tasks: List<ActivityTask>)
    data class ActivityTask(val category: String, val description: String)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_planning_calendar, container, false)
        val spinner = view.findViewById<AutoCompleteTextView>(R.id.spinnerMonth)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerTasks)

        val reader = InputStreamReader(requireContext().assets.open("calendar.json"))
        allActivities = Gson().fromJson(reader, object : TypeToken<List<MonthActivities>>() {}.type)

        val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        spinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, months))

        adapter = TaskAdapter(displayedTasks)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        spinner.setOnItemClickListener { _, _, position, _ ->
            val month = months[position]
            displayedTasks.clear()
            allActivities.find { it.month == month }?.tasks?.let { displayedTasks.addAll(it) }
            adapter.notifyDataSetChanged()
        }

        return view
    }

    class TaskAdapter(private val list: List<ActivityTask>) : RecyclerView.Adapter<TaskAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val cat: TextView = v.findViewById(android.R.id.text1)
            val desc: TextView = v.findViewById(android.R.id.text2)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
            return VH(v)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.cat.text = item.category
            holder.desc.text = item.description
        }
        override fun getItemCount() = list.size
    }
}
