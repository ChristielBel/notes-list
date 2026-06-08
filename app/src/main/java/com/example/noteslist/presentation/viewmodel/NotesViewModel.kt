package com.example.noteslist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.datastore.MAX_VISIBLE
import com.example.noteslist.data.datastore.SPACING
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.usecase.FormatDateUseCase
import com.example.noteslist.domain.usecase.NotesUseCase
import com.example.noteslist.domain.usecase.SettingsUseCase
import com.example.noteslist.presentation.item.NotesUiItem
import com.example.noteslist.presentation.state.NotesState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class NotesViewModel(
    private val notesUseCase: NotesUseCase,
    private val formatDateUseCase: FormatDateUseCase,
    settingsUseCase: SettingsUseCase,
) : ViewModel() {

    private val _expandedStacks = MutableStateFlow<Set<String>>(emptySet())
    private val expandedStacks: StateFlow<Set<String>> = _expandedStacks

    private val _query = MutableStateFlow("")
    fun setQuery(value: String) {
        _query.value = value
    }

    private val isFirstLoad = MutableStateFlow(true)
    private var firstLoadStartTime = 0L

    init {
        firstLoadStartTime = System.currentTimeMillis()

        viewModelScope.launch {
            notesUseCase.init()
            isFirstLoad.value = false
        }
    }

    val uiItems: StateFlow<NotesState> =
        combine(
            notesUseCase.notesFlow,
            settingsUseCase.settingsFlow,
            _expandedStacks,
            _query
                .debounce(250)
                .map { it.trim() }
                .distinctUntilChanged(),
            isFirstLoad,
        ) { notes, settings, expanded, query, firstLoad ->

            val filtered = if (query.isBlank()) {
                notes
            } else {
                notes.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            val elapsed = System.currentTimeMillis() - firstLoadStartTime
            val minTimePassed = elapsed >= 500

            val showShimmer = firstLoad && !minTimePassed

            NotesState(
                items = rebuild(filtered, expanded),
                stackSpacing = settings.stackSpacing,
                stackMaxVisible = settings.stackMaxVisible,
                showShimmer = showShimmer
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotesState(
                emptyList(),
                SPACING,
                MAX_VISIBLE,
                true,
            )
        )

    fun toggleRead(note: Note) {
        viewModelScope.launch {
            notesUseCase.toggleRead(note)
        }
    }

    fun toggleStack(id: String) {
        _expandedStacks.value =
            if (_expandedStacks.value.contains(id)) {
                _expandedStacks.value - id
            } else {
                _expandedStacks.value + id
            }
    }

    fun rebuild(
        notes: List<Note>,
        expandedStacks: Set<String>,
    ): List<NotesUiItem> {

        val result = mutableListOf<NotesUiItem>()

        val grouped = notes
            .groupBy { it.createdAt.toLocalDate() }
            .toSortedMap(compareByDescending { it })

        grouped.forEach { (date, notesForDate) ->

            result.add(
                NotesUiItem.DateHeader(
                    text = formatDateUseCase.execute(date)
                )
            )

            val important = notesForDate.filter { it.isImportant }
            val normal = notesForDate.filter { !it.isImportant }

            important.forEach { note ->
                result.add(NotesUiItem.Important(note))
            }

            if (normal.isNotEmpty()) {
                val stackId = "stack_$date"

                result.add(
                    NotesUiItem.Stack(
                        id = stackId,
                        notes = normal,
                        expanded = expandedStacks.contains(stackId),
                    )
                )
            }
        }

        return result
    }
}