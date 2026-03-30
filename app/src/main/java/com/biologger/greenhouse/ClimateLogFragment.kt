package com.biologger.greenhouse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.ClimateRecord
import com.biologger.viewmodel.ScientificViewModel
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp

class ClimateLogFragment : Fragment() {
    private val viewModel: ScientificViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_climate_log, container, false)
        val editDayTemp = view.findViewById<TextInputEditText>(R.id.editDayTemp)
        val editNightTemp = view.findViewById<TextInputEditText>(R.id.editNightTemp)
        val editHumidity = view.findViewById<TextInputEditText>(R.id.editHumidity)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)
        val chart = view.findViewById<LineChart>(R.id.climateChart)

        setupChart(chart)

        viewModel.repository.getAllClimateRecords().observe(viewLifecycleOwner) { data ->
            updateChart(chart, data)
        }

        btnSave.setOnClickListener {
            val tDay = editDayTemp.text.toString().toDoubleOrNull() ?: 0.0
            val tNight = editNightTemp.text.toString().toDoubleOrNull() ?: 0.0
            val rh = editHumidity.text.toString().toDoubleOrNull() ?: 0.0
            val svp = 0.6108 * exp(17.27 * tDay / (tDay + 237.3))
            val vpd = svp * (1.0 - rh / 100.0)
            val dif = tDay - tNight

            view.findViewById<TextView>(R.id.resVpd).text = "VPD: %.2f kPa".format(vpd)
            view.findViewById<TextView>(R.id.resDif).text = "DIF: %.1f °C".format(dif)
            cardResults.visibility = View.VISIBLE

            viewModel.insertClimateRecord(ClimateRecord(
                location = view.findViewById<TextInputEditText>(R.id.editLocation).text.toString(),
                dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                tempDay = tDay, tempNight = tNight, humidity = rh, co2 = view.findViewById<TextInputEditText>(R.id.editCo2).text.toString().toDoubleOrNull() ?: 0.0,
                lightIntensity = 0.0, lightHours = 0.0, soilTemp = 0.0, ec = 0.0, ph = 0.0, vpd = vpd, dif = dif, gdd = 0.0, notes = null
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

    private fun setupChart(chart: LineChart) {
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.xAxis.textColor = Color.WHITE
        chart.axisLeft.textColor = Color.WHITE
        chart.axisRight.isEnabled = false
        chart.legend.textColor = Color.WHITE
    }

    private fun updateChart(chart: LineChart, data: List<ClimateRecord>) {
        if (data.isEmpty()) return

        val entriesTemp = data.take(10).reversed().mapIndexed { i, r -> Entry(i.toFloat(), r.tempDay.toFloat()) }
        val entriesHumid = data.take(10).reversed().mapIndexed { i, r -> Entry(i.toFloat(), r.humidity.toFloat()) }

        val setTemp = LineDataSet(entriesTemp, "Temp (°C)").apply {
            color = Color.CYAN
            setCircleColor(Color.CYAN)
            lineWidth = 2f
            valueTextColor = Color.WHITE
        }

        val setHumid = LineDataSet(entriesHumid, "Humidity (%)").apply {
            color = Color.YELLOW
            setCircleColor(Color.YELLOW)
            lineWidth = 2f
            valueTextColor = Color.WHITE
        }

        chart.data = LineData(setTemp, setHumid)
        chart.invalidate()
    }
}
