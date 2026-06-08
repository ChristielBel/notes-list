package com.example.noteslist.presentation.ui.delegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.item.BaseListItem
import com.example.noteslist.presentation.item.NotesUiItem

interface AdapterDelegate {

    fun isForViewType(item: BaseListItem): Boolean

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: BaseListItem
    )
}