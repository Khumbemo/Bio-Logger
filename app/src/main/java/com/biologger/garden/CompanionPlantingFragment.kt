package com.biologger.garden
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.biologger.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class CompanionPlantingFragment : Fragment() {

    private lateinit var data: List<CompanionData>

    data class CompanionData(
        val crop: String,
        val companions: List<String>,
        val antagonists: List<String>,
        val notes: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_companion_planting, container, false)
        val spinner = view.findViewById<AutoCompleteTextView>(R.id.spinnerPrimaryCrop)
        val card = view.findViewById<MaterialCardView>(R.id.cardCompanionInfo)
        val cgGood = view.findViewById<ChipGroup>(R.id.chipGroupGood)
        val cgBad = view.findViewById<ChipGroup>(R.id.chipGroupBad)
        val textNotes = view.findViewById<TextView>(R.id.textCompanionNotes)

        val reader = InputStreamReader(requireContext().assets.open("companions.json"))
        data = Gson().fromJson(reader, object : TypeToken<List<CompanionData>>() {}.type)

        val names = data.map { it.crop }.toTypedArray()
        spinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names))

        spinner.setOnItemClickListener { _, _, position, _ ->
            val name = names[position]
            val info = data.find { it.crop == name }
            info?.let {
                cgGood.removeAllViews()
                it.companions.forEach { c -> cgGood.addView(Chip(context).apply { text = c; isClickable = false }) }

                cgBad.removeAllViews()
                it.antagonists.forEach { a -> cgBad.addView(Chip(context).apply { text = a; isClickable = false }) }

                textNotes.text = it.notes
                card.visibility = View.VISIBLE
            }
        }

        return view
    }
}
