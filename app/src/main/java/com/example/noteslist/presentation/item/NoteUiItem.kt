package com.example.noteslist.presentation.item

import com.example.noteslist.domain.model.Note

sealed class NotesUiItem : BaseListItem {

    data class DateHeader(
        val text: String
    ) : NotesUiItem() {
        override val id: String = text
    }

    data class Important(
        val note: Note
    ) : NotesUiItem() {
        override val id: String = note.id.toString()
    }

    data class Stack(
        override val id: String,
        val notes: List<Note>,
        val expanded: Boolean,
    ) : NotesUiItem()
}