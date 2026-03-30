package com.biologger.forest

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.HerbariumSpecimen
import com.biologger.notevault.NewNoteFragment
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class DigitalHerbariumFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()
    private val photoUris = mutableListOf<String>()
    private lateinit var photoAdapter: NewNoteFragment.PhotoUriAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_digital_herbarium, container, false)
        val pick = registerForActivityResult(ActivityResultContracts.GetContent()) { it?.let {
            photoUris.add(it.toString())
            photoAdapter.notifyItemInserted(photoUris.size - 1)
        }}

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerPhotos)
        photoAdapter = NewNoteFragment.PhotoUriAdapter(photoUris)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = photoAdapter

        view.findViewById<MaterialButton>(R.id.btnTakePhoto).setOnClickListener { pick.launch("image/*") }
        view.findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            viewModel.insertHerbariumSpecimen(HerbariumSpecimen(
                specimenId = "HERB-${System.currentTimeMillis()}", family = view.findViewById<EditText>(R.id.editFamily).text.toString(),
                genus = view.findViewById<EditText>(R.id.editGenus).text.toString(), species = view.findViewById<EditText>(R.id.editSpecies).text.toString(),
                authority = null, commonName = view.findViewById<EditText>(R.id.editCommonName).text.toString(),
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()), collector = "User", locality = "Plot",
                habitat = "", habit = "Tree", parts = "Leaf", photoPaths = photoUris.joinToString(","), notes = null, latitude = null, longitude = null
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