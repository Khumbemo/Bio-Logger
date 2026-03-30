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
import com.biologger.data.WateringSchedule
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class WateringScheduleFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_watering_schedule, container, false)
        val editTemp = view.findViewById<TextInputEditText>(R.id.editTemp)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalc)
        val cardRes = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnCalc.setOnClickListener {
            val t = editTemp.text.toString().toDoubleOrNull() ?: 25.0
            val vol = 4.0 * (1 + (t - 25) * 0.02)

            view.findViewById<TextView>(R.id.resVol).text = "Daily Need: %.1f L/m²".format(vol)
            cardRes.visibility = View.VISIBLE

            viewModel.insertWateringSchedule(WateringSchedule(
                crop = view.findViewById<TextInputEditText>(R.id.editCrop).text.toString(),
                stage = "Vegetative", netDailyNeed = vol, interval = 2, volume = vol * 2, scheduleJson = ""
            ))
            Toast.makeText(context, "Watering schedule saved", Toast.LENGTH_SHORT).show()
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
