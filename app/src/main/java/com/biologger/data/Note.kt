package com.biologger.data
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.os.Bundle

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val moduleTag: String, // Unlinked/Forest/Greenhouse/Garden
    val linkedTool: String?,
    val latitude: Double?,
    val longitude: Double?,
    val dateCreated: String,
    val dateModified: String,
    val photoPaths: String? // comma-separated file paths
)
