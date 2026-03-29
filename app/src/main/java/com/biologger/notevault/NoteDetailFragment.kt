package com.biologger.notevault

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.Note
import com.biologger.viewmodel.NoteViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailFragment : Fragment() {

    private val viewModel: NoteViewModel by viewModels()
    private var currentNote: Note? = null
    private val photoUris = mutableListOf<String>()
    private lateinit var photoAdapter: NewNoteFragment.PhotoUriAdapter
    private var photoFileUri: Uri? = null

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
        val view = inflater.inflate(R.layout.fragment_note_detail, container, false)

        val noteId = arguments?.getInt("noteId") ?: -1

        val textTitle = view.findViewById<EditText>(R.id.textDetailTitle)
        val textDate = view.findViewById<TextView>(R.id.textDetailDate)
        val chipModule = view.findViewById<Chip>(R.id.chipDetailModule)
        val textTool = view.findViewById<TextView>(R.id.textDetailLinkedTool)
        val textLocation = view.findViewById<TextView>(R.id.textDetailLocation)
        val editBody = view.findViewById<EditText>(R.id.editDetailBody)
        val recyclerPhotos = view.findViewById<RecyclerView>(R.id.recyclerDetailPhotos)

        photoAdapter = NewNoteFragment.PhotoUriAdapter(photoUris)
        recyclerPhotos.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerPhotos.adapter = photoAdapter

        viewModel.getNoteById(noteId) { note ->
            note?.let {
                currentNote = it
                requireActivity().runOnUiThread {
                    textTitle.setText(it.title)
                    textDate.text = "Created: ${it.dateCreated}\nModified: ${it.dateModified}"
                    chipModule.text = it.moduleTag
                    textTool.text = "Linked tool: ${it.linkedTool ?: "None"}"
                    textLocation.text = "GPS: ${if (it.latitude != null) "${it.latitude}, ${it.longitude}" else "Not set"}"
                    editBody.setText(it.body)

                    it.photoPaths?.split(",")?.forEach { uriString ->
                        if (uriString.isNotBlank() && !photoUris.contains(uriString)) {
                            photoUris.add(uriString)
                        }
                    }
                    photoAdapter.notifyDataSetChanged()
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btnDetailTakePhoto).setOnClickListener {
            dispatchTakePictureIntent()
        }

        view.findViewById<MaterialButton>(R.id.btnDetailAddPhoto).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        view.findViewById<MaterialButton>(R.id.btnSaveChanges).setOnClickListener {
            val note = currentNote ?: return@setOnClickListener
            val updatedTitle = textTitle.text.toString()
            val updatedBody = editBody.text.toString()

            if (updatedTitle.isBlank() || updatedBody.isBlank()) {
                Toast.makeText(context, "Title and body cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedNote = note.copy(
                title = updatedTitle,
                body = updatedBody,
                dateModified = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                photoPaths = if (photoUris.isEmpty()) null else photoUris.joinToString(",")
            )

            viewModel.update(updatedNote)
            Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<MaterialButton>(R.id.btnDeleteNote).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete") { _, _ ->
                    currentNote?.let { viewModel.delete(it) }
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return view
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
}
