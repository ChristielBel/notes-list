package com.example.noteslist.presentation.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.noteslist.App
import com.example.noteslist.R
import com.example.noteslist.component.EditorComponent
import com.example.noteslist.domain.mapper.toDomain
import com.example.noteslist.domain.model.NoteParcel
import com.example.noteslist.presentation.ui.screen.EditorScreen
import com.example.noteslist.presentation.ui.theme.NotesTheme
import com.example.noteslist.presentation.viewmodel.EditorViewModel

class EditorFragment : Fragment() {

    private val viewModel: EditorViewModel by viewModels {
        val app = requireActivity().application as App
        val component = EditorComponent(app.appComponent)
        component.provideFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parcel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("note", NoteParcel::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("note")
        }

        val note = parcel?.toDomain()

        viewModel.init(note)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NotesTheme {
                    val state by viewModel.state.collectAsState()

                    EditorScreen(
                        state = state,
                        onTitleChange = viewModel::onTitleChange,
                        onContentChange = viewModel::onContentChange,
                        onImportantChange = viewModel::onImportantChange,
                        onReadChange = viewModel::onReadChange,
                        onSave = {
                            viewModel.onSave {
                                findNavController().popBackStack()
                            }
                        },
                        onBack = { handleExitRequest() }
                    )
                }
            }
        }
    }

    fun showExitDialog(onConfirm: () -> Unit) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Внимание")
            .setMessage("Данные будут потеряны. Закрыть?")
            .setPositiveButton("Да") { _, _ -> onConfirm() }
            .setNegativeButton("Нет", null)
            .show()

        val blue = requireContext().getColor(R.color.button_color)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(blue)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(blue)
    }

    fun handleExitRequest() {
        if (viewModel.state.value.hasChanges) {
            showExitDialog {
                closeEditor()
            }
        } else {
            closeEditor()
        }
    }

    private fun closeEditor() {
        if (isLandscape()) {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commit()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE
    }
}

