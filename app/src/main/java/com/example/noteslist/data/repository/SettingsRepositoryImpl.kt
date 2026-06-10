package com.example.noteslist.data.repository

import com.example.noteslist.data.datastore.SettingsDataStore
import com.example.noteslist.domain.model.Settings
import com.example.noteslist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val settingsFlow: Flow<Settings> = dataStore.settingsFlow

    override suspend fun setStackSpacing(value: Int) {
        dataStore.setStackSpacing(value)
    }

    override suspend fun setStackMaxVisible(value: Int) {
        dataStore.setStackMaxVisible(value)
    }
}