package com.biologger.notevault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.viewmodel.NoteViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup

class NoteVaultHomeFragment : Fragment() {

    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_vault_home, container, false)

        view.findViewById<MaterialButton>(R.id.btnNewNote).setOnClickListener {
            findNavController().navigate(R.id.action_noteVaultHomeFragment_to_newNoteFragment)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerNotes)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NoteAdapter { note ->
            val bundle = Bundle().apply {
                putInt("noteId", note.id)
            }
            findNavController().navigate(R.id.action_noteVaultHomeFragment_to_noteDetailFragment, bundle)
        }
        recyclerView.adapter = adapter

        viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
        }

        view.findViewById<ChipGroup>(R.id.chipGroupFilter).setOnCheckedChangeListener { _, checkedId ->
            val module = when (checkedId) {
                R.id.chipForest -> "Forest"
                R.id.chipGreenhouse -> "Greenhouse"
                R.id.chipGarden -> "Garden"
                R.id.chipUnlinked -> "Unlinked"
                else -> null
            }

            if (module != null) {
                viewModel.getNotesByModule(module).observe(viewLifecycleOwner) { notes ->
                    adapter.submitList(notes)
                }
            } else {
                viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
                    adapter.submitList(notes)
                }
            }
        }

        return view
    }
}
