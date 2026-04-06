package com.biologger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.view.animation.DecelerateInterpolator

class HomeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocation: TextView
    private lateinit var tvAltitude: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLastLocation()
            } else {
                tvLocation.text = "Permission denied"
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        tvLocation = view.findViewById(R.id.tvLocation)
        tvAltitude = view.findViewById(R.id.tvAltitude)

        checkLocationPermission()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Modern scientific entrance animation
        view.alpha = 0f
        view.translationY = 80f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator(1.5f))
            .start()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    tvLocation.text = String.format("%.4f, %.4f", it.latitude, it.longitude)
                    tvAltitude.text = String.format("%.1fm ASL", it.altitude)
                } ?: run {
                    tvLocation.text = "Location unavailable"
                }
            }
        } catch (e: SecurityException) {
            tvLocation.text = "Permission error"
        }
    }
}
