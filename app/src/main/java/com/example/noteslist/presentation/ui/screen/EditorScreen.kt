package com.example.noteslist.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.noteslist.R
import com.example.noteslist.presentation.state.EditorState
import com.example.noteslist.presentation.ui.TitleError
import com.example.noteslist.presentation.ui.theme.BluePrimary
import com.example.noteslist.presentation.ui.theme.ErrorColor

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        isError = isError,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colors.onSurface,

            backgroundColor = Color.Transparent,

            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),

            errorBorderColor = ErrorColor,
            errorLabelColor = ErrorColor,
            errorCursorColor = ErrorColor,

            focusedLabelColor = BluePrimary,
            unfocusedLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),

            cursorColor = BluePrimary
        )
    )
}

@Composable
fun AppCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = BluePrimary,
                uncheckedColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                checkmarkColor = Color.White
            )
        )

        Text(
            text = text,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun EditorScreen(
    state: EditorState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onImportantChange: (Boolean) -> Unit,
    onReadChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {

    BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        AppOutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.heading)) },
            isError = state.titleError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        state.titleError?.let { error ->
            val message = when (error) {
                TitleError.EMPTY -> stringResource(R.string.must_be_filled_out)
                TitleError.TOO_LONG -> stringResource(R.string.is_too_long_message)
            }

            Text(
                text = message,
                color = ErrorColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        AppOutlinedTextField(
            value = state.content,
            onValueChange = onContentChange,
            label = { Text(stringResource(R.string.note_text)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            maxLines = 5
        )

        AppCheckboxRow(
            checked = state.isImportant,
            onCheckedChange = onImportantChange,
            text = stringResource(R.string.important),
            modifier = Modifier.padding(top = 12.dp)
        )

        if (state.isEdit) {
            Text(
                text = stringResource(R.string.create_at, state.formattedDate ?: ""),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(top = 12.dp),
            )

            AppCheckboxRow(
                checked = state.isRead,
                onCheckedChange = onReadChange,
                text = stringResource(R.string.read)
            )
        }

        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = BluePrimary,
                contentColor = Color.White
            ),
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                if (state.isEdit) stringResource(R.string.save)
                else stringResource(R.string.add)
            )
        }
    }
}