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

    inner class RecordBinder : Binder() {
        private val recorder = AudioRecorder.Builder().build()
        val volume
            get() = recorder.volume
        val state
            get() = recorder.state

        fun startRecording() {
            recorder.startRecording()
        }

        fun stopRecording() {
            recorder.stopRecording()
        }

        fun releaseResources() {
            recorder.release()
        }

        fun resumeRecording() {
            recorder.resume()
        }
    }
}