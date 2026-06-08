package com.example.noteslist.presentation.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.noteslist.presentation.item.BaseListItem
import com.example.noteslist.presentation.item.NotesUiItem
import com.example.noteslist.presentation.payload.NoteStackPayload

object NotesDiffCallback : DiffUtil.ItemCallback<BaseListItem>() {

    override fun areItemsTheSame(
        oldItem: BaseListItem,
        newItem: BaseListItem
    ): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(
        oldItem: BaseListItem,
        newItem: BaseListItem
    ): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }

    override fun getChangePayload(
        oldItem: BaseListItem,
        newItem: BaseListItem
    ): Any? {

        if (oldItem is NotesUiItem.Stack && newItem is NotesUiItem.Stack) {

            if (oldItem.expanded != newItem.expanded) {
                return NoteStackPayload.ExpandChanged(newItem.expanded)
            }
        }

        return null
    }
}