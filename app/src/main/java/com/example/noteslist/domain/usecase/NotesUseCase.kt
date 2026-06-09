package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.repository.NoteRepository

class NotesUseCase(
    private val repository: NoteRepository
) {

    val notesFlow = repository.observeNotes()

    suspend fun init() {
        repository.initIfEmpty()
    }

    suspend fun toggleRead(note: Note) {
        repository.updateNote(
            note.copy(isRead = !note.isRead)
        )
    }

    suspend fun save(note: Note) {
        repository.updateNote(note)
    }
}