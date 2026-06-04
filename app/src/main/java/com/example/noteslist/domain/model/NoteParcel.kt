package com.example.noteslist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteParcel(
    val id: Long,
    val title: String,
    val text: String,
    val createdAt: String,
    val isImportant: Boolean,
    val isRead: Boolean,
) : Parcelable