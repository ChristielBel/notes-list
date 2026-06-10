package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.repository.SettingsRepository

class SettingsUseCase(
    private val repository: SettingsRepository
) {
    val settingsFlow = repository.settingsFlow

    suspend fun setSpacing(value: Int) {
        repository.setStackSpacing(value)
    }

    suspend fun setMaxVisible(value: Int) {
        repository.setStackMaxVisible(value)
    }
}