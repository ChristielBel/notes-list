package com.example.noteslist.domain.model

import java.time.LocalDateTime

data class Note(
    val id: Long,
    val title: String,
    val text: String,
    val createdAt: LocalDateTime,
    val isImportant: Boolean,
    val isRead: Boolean,
)