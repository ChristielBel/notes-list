package com.example.noteslist.domain.mapper

import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.NoteParcel
import java.time.LocalDateTime

fun Note.toParcel(): NoteParcel {
    return NoteParcel(
        id = id,
        title = title,
        text = text,
        createdAt = createdAt.toString(),
        isImportant = isImportant,
        isRead = isRead
    )
}

fun NoteParcel.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        text = text,
        createdAt = LocalDateTime.parse(createdAt),
        isImportant = isImportant,
        isRead = isRead
    )
}
