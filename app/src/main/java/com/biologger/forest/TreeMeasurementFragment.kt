package com.biologger.forest

<<<<<<< HEAD
import android.content.Context
=======
>>>>>>> master
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
=======
import android.widget.TextView
import android.widget.Toast
>>>>>>> master
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.biologger.R
import com.biologger.data.Note
import com.biologger.viewmodel.NoteViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.pow

class TreeMeasurementFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tree_measurement, container, false)

        val editTreeId = view.findViewById<TextInputEditText>(R.id.editTreeId)
        val editDbh = view.findViewById<TextInputEditText>(R.id.editDbh)
        val editHeight = view.findViewById<TextInputEditText>(R.id.editHeight)
        val editCrown = view.findViewById<TextInputEditText>(R.id.editCrown)
        val textBasalArea = view.findViewById<TextView>(R.id.textBasalArea)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveMeasurement)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val dbh = editDbh.text.toString().toDoubleOrNull() ?: 0.0
                if (dbh > 0) {
                    // Basal Area (m²) = PI * (DBH/200)²  (DBH is in cm)
                    val basalArea = PI * (dbh / 200.0).pow(2)
                    textBasalArea.text = "Basal Area: %.4f m²".format(basalArea)
                } else {
                    textBasalArea.text = "Basal Area: 0.0000 m²"
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        editDbh.addTextChangedListener(textWatcher)

        btnSave.setOnClickListener {
            val treeId = editTreeId.text.toString()
            val dbh = editDbh.text.toString().toDoubleOrNull()
            val height = editHeight.text.toString().toDoubleOrNull()
            val crown = editCrown.text.toString().toDoubleOrNull()

            if (treeId.isBlank() || dbh == null || height == null) {
                Toast.makeText(context, "Please fill in ID, DBH and Height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbh < 0 || height < 0) {
                Toast.makeText(context, "Measurements cannot be negative", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val basalArea = PI * (dbh / 200.0).pow(2)

            val observationBody = """
                Tree ID: $treeId
                DBH: $dbh cm
                Height: $height m
                Crown Diameter: ${crown ?: "N/A"} m
                Calculated Basal Area: %.4f m²
            """.trimIndent().format(basalArea)

            val note = Note(
                title = "Measurement: Tree $treeId",
                body = observationBody,
                moduleTag = "Forest",
                linkedTool = "Tree Measurement",
                latitude = null, // Will be auto-filled if we had GPS context here, but keeping it simple for now
                longitude = null,
                dateCreated = timestamp,
                dateModified = timestamp,
                photoPaths = null
            )

            noteViewModel.insert(note)
            Toast.makeText(context, "Measurement saved to Note Vault", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

<<<<<<< HEAD
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

=======
>>>>>>> master
        return view
    }
}
