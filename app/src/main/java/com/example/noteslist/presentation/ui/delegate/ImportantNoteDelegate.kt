package com.example.noteslist.presentation.ui.delegate

import android.view.HapticFeedbackConstants
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.Note
import com.example.noteslist.presentation.item.BaseListItem
import com.example.noteslist.presentation.item.NotesUiItem
import com.example.noteslist.presentation.ui.view.NoteView

class ImportantNoteDelegate(
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit
) : AdapterDelegate {

    override fun isForViewType(item: BaseListItem) =
        item is NotesUiItem.Important

    override fun onCreateViewHolder(parent: ViewGroup)
            : RecyclerView.ViewHolder {

        val view = NoteView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: BaseListItem
    ) {

        val note = (item as NotesUiItem.Important).note
        val view = holder.itemView as NoteView

        view.render(note)

        view.setOnClickListener {
            onClick(note)
        }

        view.setOnLongClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            onLongClick(note)
            true
        }
    }
}