package com.example.noteslist.domain.repository

import com.example.noteslist.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<Settings>

    suspend fun setStackSpacing(value: Int)
    suspend fun setStackMaxVisible(value: Int)
}