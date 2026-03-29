package com.biologger.notevault

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EdgeEffect
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.Note
import com.biologger.viewmodel.NoteViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewNoteFragment : Fragment() {

    private val viewModel: NoteViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private val photoUris = mutableListOf<String>()
    private lateinit var photoAdapter: PhotoUriAdapter
    private var photoFileUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fetchLocation()
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoFileUri?.let { uri ->
                photoUris.add(uri.toString())
                photoAdapter.notifyItemInserted(photoUris.size - 1)
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            photoUris.add(it.toString())
            photoAdapter.notifyItemInserted(photoUris.size - 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_note, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val editTitle = view.findViewById<TextInputEditText>(R.id.editNoteTitle)
        val editBody = view.findViewById<TextInputEditText>(R.id.editNoteBody)
        val spinnerModule = view.findViewById<Spinner>(R.id.spinnerModule)
        val spinnerTool = view.findViewById<Spinner>(R.id.spinnerTool)
        val textLocation = view.findViewById<TextView>(R.id.textLocation)
        val recyclerPhotos = view.findViewById<RecyclerView>(R.id.recyclerPhotos)

        // Setup Photo RecyclerView
        photoAdapter = PhotoUriAdapter(photoUris)
        recyclerPhotos.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerPhotos.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int) = EdgeEffect(view.context).apply {
                color = Color.TRANSPARENT
            }
        }
        recyclerPhotos.adapter = photoAdapter

        val modules = arrayOf("Unlinked", "Forest", "Greenhouse", "Garden")
        spinnerModule.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, modules)

        spinnerModule.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedModule = modules[position]
                val tools = when (selectedModule) {
                    "Forest" -> arrayOf("Tree Measurement", "Basal Area", "Stand Density", "Timber Volume", "Biomass Estimator", "Tree Health Log", "Crown Cover", "Litterfall Log", "Quadrat Studies", "Transect Studies", "GPS and Mapping", "Environmental Variables", "Disturbance Index", "Digital Herbarium")
                    "Greenhouse" -> arrayOf("Pot Experiment", "Pot Logger", "Climate Log", "Germination Tracker", "Fertilizer Calculator", "Irrigation Scheduler", "Pest and Disease", "Harvest Recorder", "Growth Rate Analyzer")
                    "Garden" -> arrayOf("Seasonal Planner", "Companion Planting", "Soil pH and Amendment", "Bed Layout Planner", "Watering Schedule", "Composting Tracker", "Pest Identifier", "Yield Recorder", "Yield Estimation", "Plant Wiki", "Planning Calendar")
                    else -> arrayOf("None")
                }
                spinnerTool.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tools)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        view.findViewById<MaterialButton>(R.id.btnRefreshLocation).setOnClickListener {
            checkLocationPermissionAndFetch()
        }

        view.findViewById<MaterialButton>(R.id.btnTakePhoto).setOnClickListener {
            dispatchTakePictureIntent()
        }

        view.findViewById<MaterialButton>(R.id.btnUploadPhoto).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        view.findViewById<MaterialButton>(R.id.btnSaveNote).setOnClickListener {
            val title = editTitle.text.toString()
            val body = editBody.text.toString()

            if (title.isBlank() || body.isBlank()) {
                Toast.makeText(context, "Title and body cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val note = Note(
                title = title,
                body = body,
                moduleTag = spinnerModule.selectedItem.toString(),
                linkedTool = spinnerTool.selectedItem.toString(),
                latitude = currentLatitude,
                longitude = currentLongitude,
                dateCreated = timestamp,
                dateModified = timestamp,
                photoPaths = if (photoUris.isEmpty()) null else photoUris.joinToString(",")
            )

            viewModel.insert(note)
            findNavController().popBackStack()
        }

        view.findViewById<MaterialButton>(R.id.btnDiscard).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard Note")
                .setMessage("Are you sure you want to discard this note?")
                .setPositiveButton("Discard") { _, _ -> findNavController().popBackStack() }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Auto-fetch location on start
        checkLocationPermissionAndFetch()

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

    private fun checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                    view?.findViewById<TextView>(R.id.textLocation)?.text = "Location: ${it.latitude}, ${it.longitude}"
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }
        photoFile?.also {
            photoFileUri = FileProvider.getUriForFile(
                requireContext(),
                "com.biologger.fileprovider",
                it
            )
            takePhotoLauncher.launch(photoFileUri!!)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    class PhotoUriAdapter(private val uris: List<String>) : RecyclerView.Adapter<PhotoUriAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.imageThumbnail)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_thumbnail, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.imageView.setImageURI(Uri.parse(uris[position]))
        }
        override fun getItemCount() = uris.size
    }
}
