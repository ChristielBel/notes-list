package com.example.noteslist.domain.repository

import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun initIfEmpty()
}