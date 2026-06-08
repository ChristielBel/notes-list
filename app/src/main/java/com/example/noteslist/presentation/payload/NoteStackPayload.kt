package com.example.noteslist.presentation.payload

sealed class NoteStackPayload {
    data class ExpandChanged(val expanded: Boolean) : NoteStackPayload()
}