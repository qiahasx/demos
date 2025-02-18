package com.example.record

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.common.util.toast

class RecordService : Service() {
    private val binder = RecordBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        binder.releaseResources()
        val path = binder.outPath ?: return
        toast("文件保存到: $path")
    }

    inner class RecordBinder : Binder() {
        private var recorder: AudioRecorder? = null
        val volume
            get() = recorder?.volume
        val state
            get() = recorder?.state ?: AudioRecorder.RecordState.INIT
        val outPath
            get() = recorder?.outPath

        fun createRecorder(build: AudioRecorder.Builder): AudioRecorder {
            val audioRecorder = recorder
            if (audioRecorder != null) return audioRecorder
            return build.build().also { recorder = it }
        }

        fun startRecording() {
            recorder?.startRecording()
        }

        fun stopRecording() {
            recorder?.stopRecording()
        }

        fun releaseResources() {
            recorder?.release()
        }

        fun resumeRecording() {
            recorder?.resume()
        }
    }
}