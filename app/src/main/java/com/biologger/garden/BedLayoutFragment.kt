package com.biologger.garden
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
import com.biologger.R
import com.biologger.data.BedLayout
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.floor

class BedLayoutFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bed_layout, container, false)
        val editL = view.findViewById<TextInputEditText>(R.id.editL)
        val editW = view.findViewById<TextInputEditText>(R.id.editW)
        val editS = view.findViewById<TextInputEditText>(R.id.editSpacing)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalc)
        val cardRes = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnCalc.setOnClickListener {
            val l = editL.text.toString().toDoubleOrNull() ?: 0.0
            val w = editW.text.toString().toDoubleOrNull() ?: 0.0
            val s = (editS.text.toString().toDoubleOrNull() ?: 30.0) / 100.0
            if (s <= 0) return@setOnClickListener

            val count = floor(l / s) * floor(w / s)
            view.findViewById<TextView>(R.id.resPlants).text = "Est. Plants: ${count.toInt()}"
            cardRes.visibility = View.VISIBLE

            viewModel.insertBedLayout(BedLayout(
                bedName = view.findViewById<TextInputEditText>(R.id.editName).text.toString(),
                shape = "Rectangle", dimensionsJson = "", method = "Row", spacing = s,
                plantCount = count.toInt(), density = count / (l * w)
            ))
            Toast.makeText(context, "Bed layout saved", Toast.LENGTH_SHORT).show()
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
}
