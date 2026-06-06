package com.example.noteslist.data.mapper

import com.example.noteslist.data.db.NoteEntity
import com.example.noteslist.domain.model.Note
import java.time.LocalDateTime

fun NoteEntity.toDomain() = Note(
    id = id,
    title = title,
    text = text,
    createdAt = LocalDateTime.parse(createdAt),
    isImportant = isImportant,
    isRead = isRead
)

fun Note.toEntity() = NoteEntity(
    id = id,
    title = title,
    text = text,
    createdAt = createdAt.toString(),
    isImportant = isImportant,
    isRead = isRead
)