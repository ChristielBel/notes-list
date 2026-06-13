package com.example.noteslist.component

import com.example.noteslist.presentation.factory.AppViewModelFactory

class EditorComponent(
    private val appComponent: AppComponent
) {
    fun provideFactory(): AppViewModelFactory {
        return AppViewModelFactory(
            appComponent.notesUseCase,
            appComponent.formatDateUseCase,
            appComponent.settingsUseCase,
        )
    }
}