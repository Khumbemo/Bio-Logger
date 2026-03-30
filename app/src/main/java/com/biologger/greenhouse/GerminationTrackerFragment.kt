package com.biologger.greenhouse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
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
import com.biologger.data.GerminationTrial
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class GerminationTrackerFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    private val dailyCounts = mutableListOf<DayCount>()
    private lateinit var adapter: DayCountAdapter

    data class DayCount(val day: Int, var count: Int = 0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_germination_tracker, container, false)
        val editSeeds = view.findViewById<TextInputEditText>(R.id.editSeeds)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddDay)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardRes = view.findViewById<MaterialCardView>(R.id.cardResults)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCounts)

        adapter = DayCountAdapter(dailyCounts)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        btnAdd.setOnClickListener {
            dailyCounts.add(DayCount(dailyCounts.size + 1))
            adapter.notifyItemInserted(dailyCounts.size - 1)
        }

        btnSave.setOnClickListener {
            val totalSeeds = editSeeds.text.toString().toIntOrNull() ?: 100
            var germinated = 0
            var weightedSum = 0.0

            dailyCounts.forEach {
                germinated += it.count
                weightedSum += it.count * it.day
            }

            val gp = (germinated.toDouble() / totalSeeds) * 100.0
            val mgt = if (germinated > 0) weightedSum / germinated else 0.0

            view.findViewById<TextView>(R.id.resGp).text = "GP: %.1f%%".format(gp)
            view.findViewById<TextView>(R.id.resMgt).text = "MGT: %.2f days".format(mgt)
            cardRes.visibility = View.VISIBLE

            viewModel.insertGerminationTrial(GerminationTrial(
                trialId = view.findViewById<TextInputEditText>(R.id.editBatchId).text.toString(),
                crop = "", sowingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                seedsPerLot = totalSeeds,
                treatmentNamesJson = "{}", dailyCountsJson = Gson().toJson(dailyCounts),
                gpPercent = gp.toString(), mgt = mgt.toString(), gri = "", status = "Active"
            ))
            Toast.makeText(context, "Germination trial saved", Toast.LENGTH_SHORT).show()
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

    class DayCountAdapter(private val list: List<DayCount>) : RecyclerView.Adapter<DayCountAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val day: TextView = v.findViewById(R.id.textDay)
            val count: TextInputEditText = v.findViewById(R.id.editDayCount)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_germination_day, parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.day.text = "Day ${item.day}"
            holder.count.setText(if (item.count > 0) item.count.toString() else "")
            holder.count.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) item.count = holder.count.text.toString().toIntOrNull() ?: 0
            }
        }
        override fun getItemCount() = list.size
    }
}
