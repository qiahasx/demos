package com.example.record

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class RecordService : Service() {
    private val binder = RecordBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        binder.releaseResources()
    }

    inner class RecordBinder : Binder() {
        private lateinit var recorder: AudioRecorder
        val state
            get() = if (this::recorder.isInitialized) recorder.state else AudioRecorder.RecordState.INIT

        fun createRecorder(build: AudioRecorder.Builder): AudioRecorder {
            if (this::recorder.isInitialized) return recorder
            return build.build().also { recorder = it }
        }

        fun startRecording() {
            if (!this::recorder.isInitialized) return
            recorder.startRecording()
        }

        fun stopRecording() {
            if (!this::recorder.isInitialized) return
            recorder.stopRecording()
        }

        fun releaseResources() {
            if (!this::recorder.isInitialized) return
            recorder.release()
        }

        fun resumeRecording() {
            if (!this::recorder.isInitialized) return
            recorder.resume()
        }
    }
}