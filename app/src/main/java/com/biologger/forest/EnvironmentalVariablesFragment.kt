package com.biologger.forest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.EnvironmentalRecord
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp

class EnvironmentalVariablesFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_environmental_variables, container, false)

        val editTemp = view.findViewById<TextInputEditText>(R.id.editTemp)
        val editHumid = view.findViewById<TextInputEditText>(R.id.editHumidity)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        btnSave.setOnClickListener {
            val t = editTemp.text.toString().toDoubleOrNull() ?: 0.0
            val rh = editHumid.text.toString().toDoubleOrNull() ?: 0.0
            val vpd = (0.6108 * exp(17.27 * t / (t + 237.3))) * (1.0 - rh / 100.0)

            view.findViewById<TextView>(R.id.resVpd).text = "VPD: %.2f kPa".format(vpd)
            cardResults.visibility = View.VISIBLE

            viewModel.insertEnvironmentalRecord(EnvironmentalRecord(
                stationId = view.findViewById<TextInputEditText>(R.id.editStationId).text.toString(),
                dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                airTemp = t, humidity = rh, windSpeed = 0.0, windDir = "N", par = 0.0, soilTemp = 0.0,
                soilMoisture = 0.0, soilPh = 0.0, soilEc = 0.0, canopyCover = 0.0, vpd = vpd, soilStatus = "",
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
}
