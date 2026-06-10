package com.example.noteslist.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.noteslist.R
import com.example.noteslist.domain.model.Settings
import com.example.noteslist.presentation.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

const val SPACING_MIN = 24
const val SPACING_MAX = 64
const val SPACING_STEP = 8

const val MAX_VISIBLE_MIN = 2
const val MAX_VISIBLE_MAX = 6
const val MAX_VISIBLE_STEP = 3

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val settings by viewModel.settings.collectAsState(
        initial = Settings(SPACING_MIN, MAX_VISIBLE_MIN)
    )

    var spacing by remember { mutableIntStateOf(settings.stackSpacing) }
    var maxVisible by remember { mutableIntStateOf(settings.stackMaxVisible) }

    LaunchedEffect(settings) {
        spacing = settings.stackSpacing
        maxVisible = settings.stackMaxVisible
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = stringResource(R.string.spacing, spacing),
            color = MaterialTheme.colors.onSurface,
        )

        Slider(
            value = spacing.toFloat(),
            onValueChange = {
                spacing = it.roundToInt()
            },
            onValueChangeFinished = {
                viewModel.setSpacing(spacing)
            },
            valueRange = SPACING_MIN.toFloat()..SPACING_MAX.toFloat(),
            steps = SPACING_STEP,
        )

        Text(
            text = stringResource(R.string.max_visible, maxVisible),
            color = MaterialTheme.colors.onSurface,
        )

        Slider(
            value = maxVisible.toFloat(),
            onValueChange = {
                maxVisible = it.roundToInt()
            },
            onValueChangeFinished = {
                viewModel.setMaxVisible(maxVisible)
            },
            valueRange = MAX_VISIBLE_MIN.toFloat()..MAX_VISIBLE_MAX.toFloat(),
            steps = MAX_VISIBLE_STEP
        )
    }
}