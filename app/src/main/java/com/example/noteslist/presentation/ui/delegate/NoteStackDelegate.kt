package com.example.noteslist.presentation.ui.delegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.Settings
import com.example.noteslist.presentation.item.BaseListItem
import com.example.noteslist.presentation.item.NotesUiItem
import com.example.noteslist.presentation.ui.view.NoteStackView

class NoteStackDelegate(
    private val onNoteClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit,
    private val onStackClick: (String) -> Unit,
    private val getSettings: () -> Settings,
) : AdapterDelegate {

    override fun isForViewType(item: BaseListItem) =
        item is NotesUiItem.Stack

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {

        val view = NoteStackView(parent.context).apply {
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
        val stack = item as NotesUiItem.Stack
        val view = holder.itemView as NoteStackView
        val settings = getSettings()

        view.setStackSpacing(settings.stackSpacing)
        view.setMaxVisible(settings.stackMaxVisible)
        view.setExpanded(stack.expanded, animated = false)

        view.submitNotes(
            list = stack.notes,
            onNoteClick = onNoteClick,
            onNoteLongClick = onLongClick,
        )

        view.setOnStackClick {
            onStackClick(stack.id)
        }
    }
}