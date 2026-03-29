package com.biologger.notevault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.biologger.R
import com.biologger.data.Note
import com.google.android.material.chip.Chip

class NoteAdapter(private val onNoteClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view, onNoteClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NoteViewHolder(itemView: View, private val onNoteClick: (Note) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textNoteTitle)
        private val textBodyPreview: TextView = itemView.findViewById(R.id.textNoteBodyPreview)
        private val textDate: TextView = itemView.findViewById(R.id.textNoteDate)
        private val chipModule: Chip = itemView.findViewById(R.id.chipModuleTag)
        private val imageCamera: ImageView = itemView.findViewById(R.id.imageCameraIcon)

        fun bind(note: Note) {
            textTitle.text = note.title
            textBodyPreview.text = note.body
            textDate.text = note.dateModified
            chipModule.text = note.moduleTag

            val color = when (note.moduleTag) {
                "Forest" -> 0xFF2E7D32.toInt()
                "Greenhouse" -> 0xFF00796B.toInt()
                "Garden" -> 0xFFF57F17.toInt()
                else -> 0xFF6A1B9A.toInt()
            }
            chipModule.setChipBackgroundColorResource(android.R.color.transparent)
            chipModule.setTextColor(color)
            chipModule.chipStrokeColor = android.content.res.ColorStateList.valueOf(color)
            chipModule.chipStrokeWidth = 2f

            imageCamera.visibility = if (!note.photoPaths.isNullOrEmpty()) View.VISIBLE else View.GONE

            itemView.setOnClickListener { onNoteClick(note) }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
    }
}
