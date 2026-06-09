package com.example.noteslist.presentation.state

import android.os.Parcelable
import com.example.noteslist.presentation.ui.TitleError
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditorState(
    val id: Long? = null,
    val title: String = "",
    val content: String = "",
    val isImportant: Boolean = false,
    val isRead: Boolean = false,
    val createdAt: String? = null,
    val formattedDate: String? = null,
    val titleError: TitleError? = null,
    val hasChanges: Boolean = false,
    val isEdit: Boolean = false
) : Parcelable