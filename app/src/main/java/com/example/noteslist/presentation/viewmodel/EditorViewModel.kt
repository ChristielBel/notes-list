package com.example.noteslist.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.usecase.FormatDateUseCase
import com.example.noteslist.domain.usecase.NotesUseCase
import com.example.noteslist.presentation.state.EditorState
import com.example.noteslist.presentation.ui.TitleError
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val TITLE_SYMBOL_LIMIT = 50
private const val STATE_KEY = "editor_state"
private const val ORIGINAL_KEY = "editor_original_state"

class EditorViewModel(
    private val notesUseCase: NotesUseCase,
    private val formatDateUseCase: FormatDateUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val state: StateFlow<EditorState> =
        savedStateHandle.getStateFlow(STATE_KEY, EditorState())

    private var originalState: EditorState?
        get() = savedStateHandle[ORIGINAL_KEY]
        set(value) {
            savedStateHandle[ORIGINAL_KEY] = value
        }

    fun init(note: Note?) {
        val current = state.value

        if (current.title.isNotBlank() || current.content.isNotBlank()) return

        val newState = if (note != null) {
            EditorState(
                id = note.id,
                title = note.title,
                content = note.text,
                isImportant = note.isImportant,
                isRead = note.isRead,
                createdAt = note.createdAt.toString(),
                formattedDate = formatDateUseCase.formatDateTime(note.createdAt),
                isEdit = true
            )
        } else {
            EditorState()
        }

        originalState = newState
        savedStateHandle[STATE_KEY] = newState
    }

    fun onTitleChange(value: String) {
        val updated = state.value.copy(
            title = value,
            titleError = validateTitle(value)
        )
        setState(updated)
    }

    fun onContentChange(value: String) {
        setState(state.value.copy(content = value))
    }

    fun onImportantChange(value: Boolean) {
        setState(state.value.copy(isImportant = value))
    }

    fun onReadChange(value: Boolean) {
        setState(state.value.copy(isRead = value))
    }

    private fun setState(newState: EditorState) {
        val withChanges = newState.copy(
            hasChanges = calculateChanges(newState)
        )
        savedStateHandle[STATE_KEY] = withChanges
    }

    fun onSave(onSuccess: () -> Unit) {
        val current = state.value

        val error = validateTitle(current.title)
        if (error != null) {
            savedStateHandle[STATE_KEY] = current.copy(titleError = error)
            return
        }

        val note = Note(
            id = current.id ?: System.currentTimeMillis(),
            title = current.title,
            text = current.content,
            createdAt = current.createdAt?.let { LocalDateTime.parse(it) }
                ?: LocalDateTime.now(),
            isImportant = current.isImportant,
            isRead = current.isRead
        )

        viewModelScope.launch {
            notesUseCase.save(note)

            originalState = null
            savedStateHandle[STATE_KEY] = EditorState()

            onSuccess()
        }
    }

    private fun validateTitle(title: String): TitleError? {
        return when {
            title.isBlank() -> TitleError.EMPTY
            title.length > TITLE_SYMBOL_LIMIT -> TitleError.TOO_LONG
            else -> null
        }
    }

    private fun calculateChanges(current: EditorState): Boolean {
        val original = originalState ?: return false

        return current.title != original.title ||
                current.content != original.content ||
                current.isImportant != original.isImportant ||
                current.isRead != original.isRead
    }
}