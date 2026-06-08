package com.example.noteslist.presentation.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.data.datastore.MAX_VISIBLE
import com.example.noteslist.data.datastore.SPACING
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.Settings
import com.example.noteslist.presentation.item.BaseListItem
import com.example.noteslist.presentation.item.NotesUiItem
import com.example.noteslist.presentation.payload.NoteStackPayload
import com.example.noteslist.presentation.ui.delegate.DateHeaderDelegate
import com.example.noteslist.presentation.ui.delegate.ImportantNoteDelegate
import com.example.noteslist.presentation.ui.delegate.NoteStackDelegate
import com.example.noteslist.presentation.ui.view.NoteStackView

class NotesAdapter(
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit,
    onStackClick: (String) -> Unit,
) : ListAdapter<BaseListItem, RecyclerView.ViewHolder>(NotesDiffCallback) {

    private var settings: Settings = Settings(SPACING, MAX_VISIBLE)

    fun updateSettings(newSettings: Settings) {
        settings = newSettings
        notifyItemRangeChanged(0, itemCount)
    }

    private fun getSettings(): Settings = settings
    private val delegates = listOf(
        DateHeaderDelegate(),
        ImportantNoteDelegate(onNoteClick, onNoteLongClick),
        NoteStackDelegate(
            onNoteClick,
            onNoteLongClick,
            onStackClick,
            { getSettings() }
        ),
    )

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        val index = delegates.indexOfFirst {
            it.isForViewType(item)
        }

        return index
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        delegates[viewType].onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)]
            .onBindViewHolder(holder, getItem(position))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(position)

        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val payload = payloads.firstOrNull() as? NoteStackPayload

            if (payload != null && item is NotesUiItem.Stack) {
                val view = holder.itemView as NoteStackView

                when (payload) {
                    is NoteStackPayload.ExpandChanged -> {
                        view.setExpanded(payload.expanded, animated = true)
                    }
                }
            } else {
                onBindViewHolder(holder, position)
            }
        }
    }
}