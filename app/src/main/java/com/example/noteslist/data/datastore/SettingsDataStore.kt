package com.example.noteslist.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.noteslist.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")
private val STACK_SPACING = intPreferencesKey("stack_spacing")
private val STACK_MAX_VISIBLE = intPreferencesKey("stack_max_visible")

const val SPACING = 24
const val MAX_VISIBLE = 3

class SettingsDataStore(private val context: Context) {

    val settingsFlow: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            stackSpacing = prefs[STACK_SPACING] ?: SPACING,
            stackMaxVisible = prefs[STACK_MAX_VISIBLE] ?: MAX_VISIBLE
        )
    }

    suspend fun setStackSpacing(value: Int) {
        context.dataStore.edit {
            it[STACK_SPACING] = value
        }
    }

    suspend fun setStackMaxVisible(value: Int) {
        context.dataStore.edit {
            it[STACK_MAX_VISIBLE] = value
        }
    }
}