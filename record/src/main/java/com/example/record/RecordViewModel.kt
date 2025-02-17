package com.example.record

import androidx.datastore.preferences.core.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.BaseApp
import com.example.common.dataStore
import com.example.common.util.getString
import com.example.common.util.toast
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecordViewModel : ViewModel() {
    val volume = MutableLiveData<StateFlow<Int>?>(null)
    val state: AudioRecorder.RecordState
        get() = recordBinder?.state ?: AudioRecorder.RecordState.INIT
    var recordBinder: RecordService.RecordBinder? = null
        set(value) {
            volume.postValue(value?.volume)
            field = value
        }

    val encoder: StateFlow<AudioRecorder.Encoder> = BaseApp.instance.dataStore.data
        .map { prefs ->
            val index = prefs[PreferencesKeys.ENCODER] ?: 0
            AudioRecorder.Encoder.entries[index]
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AudioRecorder.Encoder.entries[0]
        )
    val sampleRate: StateFlow<String> = BaseApp.instance.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.SAMPLE_RATE] ?: "48000"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "48000"
        )

    val channels: StateFlow<AudioRecorder.Builder.Channel> = BaseApp.instance.dataStore.data
        .map { prefs ->
            val index = prefs[PreferencesKeys.CHANNELS] ?: 1
            AudioRecorder.Builder.Channel.entries.getOrNull(index) ?: AudioRecorder.Builder.Channel.MOMO
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AudioRecorder.Builder.Channel.MOMO
        )

    val enableNoiseSuppressor: StateFlow<Boolean> = BaseApp.instance.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.NOISE_SUPPRESSOR] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val enableAutomaticGain: StateFlow<Boolean> = BaseApp.instance.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.AUTOMATIC_GAIN] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val enableAutomaticEcho: StateFlow<Boolean> = BaseApp.instance.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.AUTOMATIC_ECHO] ?: false
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    fun toggleRecord() {
        val binder = recordBinder ?: return
        when (binder.state) {
            AudioRecorder.RecordState.INIT -> binder.startRecording()
            AudioRecorder.RecordState.RECORDING -> binder.stopRecording()
            AudioRecorder.RecordState.PAUSED -> binder.resumeRecording()
            AudioRecorder.RecordState.RELEASE -> {}
        }
    }

    fun releaseRecord() {
        recordBinder?.releaseResources()
    }

    fun selectEncoder(entry: AudioRecorder.Encoder) {
        if (!allowSetting()) return
        viewModelScope.launch {
            BaseApp.instance.dataStore.edit { datas ->
                datas[PreferencesKeys.ENCODER] = entry.ordinal
            }
        }
    }
    fun inputSample(sampleRate: String) {
        if (!allowSetting()) return
        viewModelScope.launch {
            BaseApp.instance.dataStore.edit { prefs ->
                prefs[PreferencesKeys.SAMPLE_RATE] = sampleRate
            }
        }
    }

    fun selectChannels(channel: AudioRecorder.Builder.Channel) {
        if (!allowSetting()) return
        viewModelScope.launch {
            BaseApp.instance.dataStore.edit { prefs ->
                prefs[PreferencesKeys.CHANNELS] = channel.ordinal
            }
        }
    }

    fun switchFeature(key: Preferences.Key<Boolean>, newValue: Boolean) {
        if (!allowSetting()) return
        viewModelScope.launch {
            BaseApp.instance.dataStore.edit { prefs ->
                when (key) {
                    PreferencesKeys.NOISE_SUPPRESSOR -> prefs[PreferencesKeys.NOISE_SUPPRESSOR] = newValue
                    PreferencesKeys.AUTOMATIC_GAIN -> prefs[PreferencesKeys.AUTOMATIC_GAIN] = newValue
                    PreferencesKeys.AUTOMATIC_ECHO -> prefs[PreferencesKeys.AUTOMATIC_ECHO] = newValue
                }
            }
        }
    }

    private fun allowSetting(): Boolean {
        if (state != AudioRecorder.RecordState.INIT) {
            toast(getString(R.string.please_before_start))
            return false
        }
        return true
    }

    object PreferencesKeys {
        val ENCODER = intPreferencesKey("encoder")
        val SAMPLE_RATE = stringPreferencesKey("sample_rate")
        val CHANNELS = intPreferencesKey("channels")
        val NOISE_SUPPRESSOR = booleanPreferencesKey("noise_suppressor")
        val AUTOMATIC_GAIN = booleanPreferencesKey("automatic_gain")
        val AUTOMATIC_ECHO = booleanPreferencesKey("automatic_echo")
    }

}