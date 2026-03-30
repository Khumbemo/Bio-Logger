package com.biologger.forest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.GpsWaypoint
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton

class GpsAndMappingFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()
    private var lastLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gps_and_mapping, container, false)
        val fused = LocationServices.getFusedLocationProviderClient(requireActivity())

        val editId = view.findViewById<EditText>(R.id.editPointId)
        val spinnerType = view.findViewById<AutoCompleteTextView>(R.id.spinnerPointType)
        val textCoords = view.findViewById<TextView>(R.id.textCoords)
        val btnCapture = view.findViewById<MaterialButton>(R.id.btnCapture)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        spinnerType.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Center", "Corner", "Tree")))

        btnCapture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fused.lastLocation.addOnSuccessListener { it?.let {
                    lastLocation = it
                    textCoords.text = "GPS: ${it.latitude}, ${it.longitude}"
                }}
            }
        }

        btnSave.setOnClickListener {
            val loc = lastLocation ?: return@setOnClickListener
            viewModel.insertGpsWaypoint(GpsWaypoint(
                pointId = editId.text.toString(), type = spinnerType.text.toString(),
                latitude = loc.latitude, longitude = loc.longitude, altitude = loc.altitude,
                accuracy = loc.accuracy, notes = null
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
