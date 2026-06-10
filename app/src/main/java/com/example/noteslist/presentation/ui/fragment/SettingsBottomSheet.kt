package com.example.noteslist.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.example.noteslist.App
import com.example.noteslist.component.SettingsComponent
import com.example.noteslist.presentation.ui.screen.SettingsScreen
import com.example.noteslist.presentation.ui.theme.NotesTheme
import com.example.noteslist.presentation.viewmodel.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: SettingsViewModel by viewModels {
        val app = requireActivity().application as App
        val component = SettingsComponent(app.appComponent)
        component.provideFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NotesTheme {
                    SettingsScreen(viewModel)
                }
            }
        }
    }
}