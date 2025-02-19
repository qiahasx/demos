package com.example.record

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.BaseApp
import com.example.common.dataStore
import com.example.common.util.getString
import com.example.common.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordViewModel : ViewModel() {
    val volume = MutableLiveData<StateFlow<Int>?>(null)
    val state: AudioRecorder.RecordState
        get() = recordBinder?.state ?: AudioRecorder.RecordState.INIT
    var recordBinder: RecordService.RecordBinder? = null

    private val _bitRate = MutableStateFlow("320000")
    val bitRate: StateFlow<String> = _bitRate

    private val _sampleRate = MutableStateFlow("48000")
    val sampleRate: StateFlow<String> = _sampleRate

    private val _encode = MutableStateFlow(AudioRecorder.Encode.entries[0])
    val encode: StateFlow<AudioRecorder.Encode> = _encode

    private val _channels = MutableStateFlow(AudioRecorder.Builder.Channel.MOMO)
    val channels: StateFlow<AudioRecorder.Builder.Channel> = _channels

    private val _enableNoiseSuppressor = MutableStateFlow(true)
    val enableNoiseSuppressor: StateFlow<Boolean> = _enableNoiseSuppressor

    private val _enableAutomaticGain = MutableStateFlow(true)
    val enableAutomaticGain: StateFlow<Boolean> = _enableAutomaticGain

    private val _enableAutomaticEcho = MutableStateFlow(false)
    val enableAutomaticEcho: StateFlow<Boolean> = _enableAutomaticEcho

    init {
        viewModelScope.launch(Dispatchers.IO) {
            BaseApp.instance.dataStore.data.collect { prefs ->
                val sampleRateValue = prefs[PreferencesKeys.SAMPLE_RATE] ?: "48000"
                _sampleRate.emit(sampleRateValue)
                val bitRareValue = prefs[PreferencesKeys.BIT_RATE] ?: "320000"
                _bitRate.emit(bitRareValue)
                val encodeIndex = prefs[PreferencesKeys.ENCODER] ?: 0
                _encode.emit(AudioRecorder.Encode.entries[encodeIndex])
                val channelIndex = prefs[PreferencesKeys.CHANNELS] ?: 1
                _channels.emit(
                    AudioRecorder.Builder.Channel.entries.getOrNull(channelIndex)
                        ?: AudioRecorder.Builder.Channel.MOMO
                )
                _enableNoiseSuppressor.emit(
                    prefs[PreferencesKeys.NOISE_SUPPRESSOR] ?: true
                )
                _enableAutomaticGain.emit(
                    prefs[PreferencesKeys.AUTOMATIC_GAIN] ?: true
                )
                _enableAutomaticEcho.emit(
                    prefs[PreferencesKeys.AUTOMATIC_ECHO] ?: false
                )
            }
        }
    }

    fun toggleRecord() {
        val binder = recordBinder ?: return
        when (binder.state) {
            AudioRecorder.RecordState.INIT -> startRecording()
            AudioRecorder.RecordState.RECORDING -> binder.stopRecording()
            AudioRecorder.RecordState.PAUSED -> binder.resumeRecording()
            else -> {}
        }
    }

    private fun startRecording() {
        val binder = recordBinder ?: return
        val builder = AudioRecorder.Builder()
            .setBitRate(bitRate.value.toInt())
            .setChannelConfig(channels.value)
            .setSampleRateInHz(sampleRate.value.toInt())
            .setEncode(encode.value)
            .setScope(viewModelScope)
            .isEnableNoiseSuppressor(enableNoiseSuppressor.value)
            .isEnableAcousticEchoCanceler(enableAutomaticEcho.value)
            .isEnableAutomaticGainControl(enableAutomaticGain.value)
        val recorder = binder.createRecorder(builder)
        volume.postValue(recorder.volume)
        binder.startRecording()
    }

    fun selectEncoder(entry: AudioRecorder.Encode) {
        if (!allowSetting()) return
        viewModelScope.launch {
            BaseApp.instance.dataStore.edit { datas ->
                datas[PreferencesKeys.ENCODER] = entry.ordinal
            }
        }
    }

    fun inputSampleRate(sampleRate: String) {
        if (!allowSetting()) return
        viewModelScope.launch {
            BaseApp.instance.dataStore.edit { prefs ->
                prefs[PreferencesKeys.SAMPLE_RATE] = sampleRate
            }
        }
    }

    fun inputBitRate(bitRate: String) {
        if (!allowSetting()) return
        viewModelScope.launch {
            BaseApp.instance.dataStore.edit { prefs ->
                prefs[PreferencesKeys.BIT_RATE] = bitRate
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

    fun allowSetting(): Boolean {
        if (state != AudioRecorder.RecordState.INIT) {
            toast(getString(R.string.please_before_start))
            return false
        }
        return true
    }

    object PreferencesKeys {
        val ENCODER = intPreferencesKey("encoder")
        val SAMPLE_RATE = stringPreferencesKey("sample_rate")
        val BIT_RATE = stringPreferencesKey("bit_rate")
        val CHANNELS = intPreferencesKey("channels")
        val NOISE_SUPPRESSOR = booleanPreferencesKey("noise_suppressor")
        val AUTOMATIC_GAIN = booleanPreferencesKey("automatic_gain")
        val AUTOMATIC_ECHO = booleanPreferencesKey("automatic_echo")
    }

}