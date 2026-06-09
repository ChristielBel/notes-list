package com.example.noteslist.presentation.item

interface BaseListItem {
    val id: String

    fun areItemsTheSame(newItem: BaseListItem): Boolean =
        this.javaClass == newItem.javaClass && id == newItem.id

    fun areContentsTheSame(newItem: BaseListItem): Boolean =
        this == newItem
}