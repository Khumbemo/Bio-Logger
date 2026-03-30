package com.biologger.viewmodel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.app.Application
import androidx.lifecycle.*
import com.biologger.data.AppDatabase
import com.biologger.data.Note
import com.biologger.data.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
    }

    fun getNotesByModule(module: String): LiveData<List<Note>> {
        return repository.getNotesByModule(module)
    }

    fun getNoteById(id: Int, callback: (Note?) -> Unit) {
        viewModelScope.launch {
            callback(repository.getNoteById(id))
        }
    }

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }
}
