package com.example.noteslist.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.noteslist.domain.usecase.FormatDateUseCase
import com.example.noteslist.domain.usecase.NotesUseCase
import com.example.noteslist.domain.usecase.SettingsUseCase
import com.example.noteslist.presentation.viewmodel.EditorViewModel
import com.example.noteslist.presentation.viewmodel.NotesViewModel
import com.example.noteslist.presentation.viewmodel.SettingsViewModel

class AppViewModelFactory(
    private val notesUseCase: NotesUseCase,
    private val formatDateUseCase: FormatDateUseCase,
    private val settingsUseCase: SettingsUseCase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {

        val savedStateHandle = extras.createSavedStateHandle()

        return when (modelClass) {
            NotesViewModel::class.java ->
                NotesViewModel(notesUseCase, formatDateUseCase, settingsUseCase)

            EditorViewModel::class.java ->
                EditorViewModel(
                    notesUseCase,
                    formatDateUseCase,
                    savedStateHandle,
                )

            SettingsViewModel::class.java ->
                SettingsViewModel(settingsUseCase)

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        } as T
    }
}