package com.example.noteslist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.usecase.SettingsUseCase
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val useCase: SettingsUseCase
) : ViewModel() {

    val settings = useCase.settingsFlow

    fun setSpacing(value: Int) {
        viewModelScope.launch {
            useCase.setSpacing(value)
        }
    }

    fun setMaxVisible(value: Int) {
        viewModelScope.launch {
            useCase.setMaxVisible(value)
        }
    }
}