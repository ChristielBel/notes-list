package com.example.noteslist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val text: String,
    val createdAt: String,
    val isImportant: Boolean,
    val isRead: Boolean,
)