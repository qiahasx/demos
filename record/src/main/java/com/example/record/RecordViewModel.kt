package com.example.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class RecordViewModel : ViewModel() {
    val volume = MutableLiveData<StateFlow<Int>?>(null)
    val state : AudioRecorder.RecordState
        get() = recordBinder?.state?: AudioRecorder.RecordState.INIT
    var recordBinder: RecordService.RecordBinder? = null
        set(value) {
            volume.postValue(value?.volume)
            field = value
        }

    fun toggleRecord() {
        val binder = recordBinder ?: return
        when (binder.state) {
            AudioRecorder.RecordState.INIT ->  binder.startRecording()
            AudioRecorder.RecordState.RECORDING -> binder.stopRecording()
            AudioRecorder.RecordState.PAUSED -> binder.resumeRecording()
            AudioRecorder.RecordState.RELEASE -> {}
        }
    }

    fun releaseRecord() {
        recordBinder?.releaseResources()
    }
}