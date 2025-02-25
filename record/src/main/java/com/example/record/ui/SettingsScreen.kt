package com.example.record.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        BitRateSetting()
        SampleRateSetting()
        val modifier = Modifier
            .align(Alignment.End)
            .padding(16.dp, 6.dp)
        ChannelSelection(modifier)
        EncoderSelection(modifier)
        AudioFeatureSwitch(NOISE_SUPPRESSOR, modifier)
        AudioFeatureSwitch(AUTOMATIC_GAIN, modifier)
        AudioFeatureSwitch(AUTOMATIC_ECHO, modifier)
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun BitRateSetting() {
    val viewModel = LocalRecordViewModel.current
    AudioParamText(
        viewModel.bitRate.value,
        "Bit Rate (/bps)"
    ) {
        viewModel.inputBitRate(it)
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun SampleRateSetting() {
    val viewModel = LocalRecordViewModel.current
    AudioParamText(
        viewModel.sampleRate.value,
        "Sample Rate (/Hz)"
    ) {
        viewModel.inputSampleRate(it)
    }
}

@Composable
fun AudioParamText(
    initValue: String?,
    label: String,
    onValueChange: (String) -> Unit
) {
    val viewModel = LocalRecordViewModel.current
    var bitRate by remember { mutableStateOf(initValue ?: "") }
    OutlinedTextField(
        value = bitRate,
        onValueChange = { input ->
            if (!viewModel.allowSetting()) return@OutlinedTextField
            val filtered = input.filter { it.isDigit() }
            val validated = filtered.ifEmpty { "" }
            bitRate = validated
            onValueChange.invoke(validated)
        },
        label = { Text(label) },
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
private fun ChannelSelection(modifier: Modifier = Modifier) {
    val viewModel = LocalRecordViewModel.current
    val channels by viewModel.channels.collectAsState()
    SettingsItem(titleRes = null, label = getString(R.string.channels)) {
        SegmentedButtonGroup(
            modifier,
            items = AudioRecorder.Builder.Channel.entries,
            selectedItem = channels,
            onItemSelected = { viewModel.selectChannels(it) }
        )
    }
}

@Composable
private fun AudioFeatureSwitch(key: Preferences.Key<Boolean>, modifier: Modifier = Modifier) {
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
            modifier = modifier,
            checked = enabled,
            onCheckedChange = { viewModel.switchFeature(key, it) },
            colors = SwitchDefaults.colors(checkedThumbColor = green_deep, checkedTrackColor = Color.White)
        )
    }
}

@Composable
private fun EncoderSelection(modifier: Modifier = Modifier) {
    val viewModel = LocalRecordViewModel.current
    val encoder by viewModel.encode.collectAsState()
    SettingsItem(titleRes = R.string.encode_type) {
        SegmentedButtonGroup(
            modifier,
            items = AudioRecorder.Encode.entries,
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
    Text(
        text = titleRes?.let { getString(it) } ?: label.orEmpty(),
        modifier = Modifier.padding(16.dp, 6.dp),
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    )
    content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SegmentedButtonGroup(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(modifier) {
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