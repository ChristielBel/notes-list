package com.example.noteslist.presentation.state

import com.example.noteslist.presentation.item.NotesUiItem

data class NotesState(
    val items: List<NotesUiItem>,
    val stackSpacing: Int,
    val stackMaxVisible: Int,
    val showShimmer: Boolean,
)