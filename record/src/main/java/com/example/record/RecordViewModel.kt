package com.example.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.util.getString
import com.example.common.util.toast
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    val encoder = MutableStateFlow(AudioRecorder.Encoder.entries[MMKV.defaultMMKV().getInt(KEY_ENCODER, 0)])

    val sample = MutableStateFlow(MMKV.defaultMMKV().getString(KEY_SAMPLE, "48000") ?: "48000")

    val channels = MutableStateFlow(AudioRecorder.Builder.Channel.entries[MMKV.defaultMMKV().getInt(KEY_CHANNELS, 1)])

    val enableNoiseSuppressor = MutableStateFlow(MMKV.defaultMMKV().getBoolean(KEY_NOISE, true))
    val enableAutomaticGain = MutableStateFlow(MMKV.defaultMMKV().getBoolean(KEY_GAIN, true))
    val enableAutomaticEcho = MutableStateFlow(MMKV.defaultMMKV().getBoolean(KEY_ECHO, false))

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
        MMKV.defaultMMKV().putInt(KEY_ENCODER, entry.ordinal)
        viewModelScope.launch {
            encoder.emit(entry)
        }
    }

    fun inputSample(sampleRate: String) {
        if (!allowSetting()) return
        MMKV.defaultMMKV().putString(KEY_SAMPLE, sampleRate)
        viewModelScope.launch {
            sample.emit(sampleRate)
        }
    }

    private fun allowSetting(): Boolean {
        if (state != AudioRecorder.RecordState.INIT) {
            toast(getString(R.string.please_before_start))
            return false
        }
        return true
    }

    fun selectChannels(channel: AudioRecorder.Builder.Channel) {
        if (!allowSetting()) return
        MMKV.defaultMMKV().putInt(KEY_CHANNELS, channel.ordinal)
        viewModelScope.launch {
            channels.emit(channel)
        }
    }

    fun switchFeature(key: String, newValue: Boolean) {
        if (!allowSetting()) return
        MMKV.defaultMMKV().putBoolean(key, newValue)
        viewModelScope.launch {
            when (key) {
                KEY_NOISE -> enableNoiseSuppressor.emit(newValue)
                KEY_GAIN -> enableAutomaticGain.emit(newValue)
                KEY_ECHO -> enableAutomaticEcho.emit(newValue)
            }
        }

    }

    companion object {
        const val KEY_ENCODER = "key_encoder"
        const val KEY_CHANNELS = "key_Channels"
        const val KEY_SAMPLE = "key_sample"
        const val KEY_NOISE = "key_noise"
        const val KEY_GAIN = "key_gain"
        const val KEY_ECHO = "key_echo"
    }
}