package com.example.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val encoder = MutableStateFlow(SettingLayout.Encoder.entries[MMKV.defaultMMKV().getInt(KEY_ENCODER, 0)])

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

    fun selectEncoder(entry: SettingLayout.Encoder) {
        MMKV.defaultMMKV().putInt(KEY_ENCODER, entry.ordinal)
        viewModelScope.launch {
            encoder.emit(entry)
        }
    }

    companion object {
        const val KEY_ENCODER = "key_encoder"
    }
}