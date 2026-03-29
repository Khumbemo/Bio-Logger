package com.biologger.data

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
