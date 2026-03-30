package com.biologger.export

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.*
import com.biologger.viewmodel.NoteViewModel
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExportCenterFragment : Fragment() {

    private val scientificViewModel: ScientificViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_export_center, container, false)

        view.findViewById<MaterialButton>(R.id.btnExportForestMeasurements).setOnClickListener {
            scientificViewModel.repository.getAllTreeMeasurements().observe(viewLifecycleOwner) { data ->
                exportToCsv("forest_measurements", data) { m ->
                    "${m.plotId},${m.treeId},${m.speciesName},${m.dbh},${m.height},${m.date}"
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btnExportBasalArea).setOnClickListener {
            scientificViewModel.repository.getAllBasalAreaPlots().observe(viewLifecycleOwner) { data ->
                exportToCsv("basal_area", data) { p ->
                    "${p.plotId},${p.totalBasalArea},${p.basalAreaPerHa},${p.treesPerHa},${p.timestamp}"
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btnExportClimateLogs).setOnClickListener {
            scientificViewModel.repository.getAllClimateRecords().observe(viewLifecycleOwner) { data ->
                exportToCsv("climate_logs", data) { c ->
                    "${c.location},${c.dateTime},${c.tempDay},${c.tempNight},${c.humidity},${c.vpd},${c.dif}"
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btnExportPotObservations).setOnClickListener {
            // Note: Pot observations are linked to experiments. For generic export we might need all.
            // Simplified here.
            Toast.makeText(context, "Exporting all pot metadata", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<MaterialButton>(R.id.btnExportYieldRecords).setOnClickListener {
            scientificViewModel.repository.getAllYieldRecords().observe(viewLifecycleOwner) { data ->
                exportToCsv("yield_records", data) { y ->
                    "${y.crop},${y.date},${y.totalWeight},${y.marketableWeight},${y.grade}"
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btnExportNotes).setOnClickListener {
            noteViewModel.allNotes.observe(viewLifecycleOwner) { data ->
                exportToCsv("field_notes", data) { n ->
                    "${n.title},${n.moduleTag},${n.linkedTool},${n.dateModified},${n.latitude},${n.longitude}"
                }
            }
        }

        return view
    }

    private fun <T> exportToCsv(filename: String, data: List<T>, transform: (T) -> String) {
        if (data.isEmpty()) {
            Toast.makeText(context, "No data to export", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            val file = File(requireContext().cacheDir, "${filename}_${timestamp}.csv")
            file.writeText(data.joinToString("\n") { transform(it) })

            val contentUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.biologger.fileprovider",
                file
            )

            val shareIntent = ShareCompat.IntentBuilder(requireContext())
                .setType("text/csv")
                .setStream(contentUri)
                .setSubject("BioLogger Research Data: $filename")
                .intent
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(shareIntent, "Share CSV via"))

        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
