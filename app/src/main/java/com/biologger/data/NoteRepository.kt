package com.biologger.data
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    fun getNotesByModule(module: String): LiveData<List<Note>> {
        return noteDao.getNotesByModule(module)
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }
}
