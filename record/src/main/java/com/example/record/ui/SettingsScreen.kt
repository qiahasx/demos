package com.example.record.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import com.example.common.util.getString
import com.example.record.AudioRecorder
import com.example.record.R
import com.example.record.RecordViewModel
import com.example.record.RecordViewModel.PreferencesKeys.AUTOMATIC_ECHO
import com.example.record.RecordViewModel.PreferencesKeys.AUTOMATIC_GAIN
import com.example.record.RecordViewModel.PreferencesKeys.NOISE_SUPPRESSOR

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 6.dp)
    ) {
        SampleRateSetting()
        ChannelSelection()
        EncoderSelection()
        AudioFeatureSwitch(NOISE_SUPPRESSOR)
        AudioFeatureSwitch(AUTOMATIC_GAIN)
        AudioFeatureSwitch(AUTOMATIC_ECHO)
    }
}

@Composable
private fun SampleRateSetting() {
    val viewModel = LocalRecordViewModel.current
    val sample by viewModel.sampleRate.collectAsState()
    OutlinedTextField(
        value = sample,
        onValueChange = { input ->
            val newValue = if (input.isEmpty()) "" else input.toIntOrNull()?.toString() ?: return@OutlinedTextField
            viewModel.inputSample(newValue)
        },
        label = { Text("Sample Rate") },
        colors = TextFieldDefaults.colors().copy(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Black,
            unfocusedContainerColor = Color.Black,
            focusedIndicatorColor = green_deep,
            unfocusedIndicatorColor = green_deep,
            focusedLabelColor = green_deep,
            unfocusedLabelColor = green_deep,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Color.Black)
    )
}

@Composable
private fun ChannelSelection() {
    val viewModel = LocalRecordViewModel.current
    val channels by viewModel.channels.collectAsState()
    SettingsItem(titleRes = null, label = getString(R.string.channels)) {
        SegmentedButtonGroup(
            items = AudioRecorder.Builder.Channel.entries,
            selectedItem = channels,
            onItemSelected = { viewModel.selectChannels(it) }
        )
    }
}

@Composable
private fun AudioFeatureSwitch(key: Preferences.Key<Boolean>) {
    val viewModel = LocalRecordViewModel.current
    val enabled by when (key) {
        NOISE_SUPPRESSOR -> viewModel.enableNoiseSuppressor.collectAsState()
        AUTOMATIC_GAIN -> viewModel.enableAutomaticGain.collectAsState()
        else -> viewModel.enableAutomaticEcho.collectAsState()
    }
    val titleRes = when (key) {
        NOISE_SUPPRESSOR -> R.string.enable_noise_suppress
        AUTOMATIC_GAIN -> R.string.enable_automatic_gain
        else -> R.string.enable_automatic_echo
    }
    SettingsItem(titleRes = titleRes) {
        Switch(
            checked = enabled,
            onCheckedChange = { viewModel.switchFeature(key, it) },
            colors = SwitchDefaults.colors(checkedThumbColor = green_deep, checkedTrackColor = Color.White)
        )
    }
}

@Composable
private fun EncoderSelection() {
    val viewModel = LocalRecordViewModel.current
    val encoder by viewModel.encoder.collectAsState()
    SettingsItem(titleRes = R.string.encode_type) {
        SegmentedButtonGroup(
            items = AudioRecorder.Encoder.entries,
            selectedItem = encoder,
            onItemSelected = {
                viewModel.selectEncoder(it)
            }
        )
    }
}

@Composable
private fun SettingsItem(
    @StringRes titleRes: Int? = null,
    label: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titleRes?.let { getString(it) } ?: label.orEmpty(),
            modifier = Modifier.weight(1f),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SegmentedButtonGroup(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        items.forEachIndexed { index, item ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = items.size
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = green_deep
                ),
                onClick = { onItemSelected(item) },
                selected = item == selectedItem,
                label = { Text(item.toString()) }
            )
        }
    }
}

val LocalRecordViewModel = compositionLocalOf<RecordViewModel> { error("") }