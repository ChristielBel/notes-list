package com.example.noteslist.presentation.ui.delegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.item.BaseListItem
import com.example.noteslist.presentation.item.NotesUiItem
import com.example.noteslist.presentation.ui.view.DateHeaderView

class DateHeaderDelegate : AdapterDelegate {

    override fun isForViewType(item: BaseListItem) =
        item is NotesUiItem.DateHeader

    override fun onCreateViewHolder(parent: ViewGroup)
            : RecyclerView.ViewHolder {

        val view = DateHeaderView(parent.context)

        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: BaseListItem
    ) {

        val header = item as NotesUiItem.DateHeader
        val view = holder.itemView as DateHeaderView

        view.bind(header.text)
    }
}