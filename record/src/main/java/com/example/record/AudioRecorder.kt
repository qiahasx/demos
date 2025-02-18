package com.example.record

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import com.example.common.BaseApp
import com.example.record.encoder.Encoder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import kotlin.math.log10

class AudioRecorder(
    private val sampleRateInHz: Int,
    private val channel: Builder.Channel,
    private val minBufferSize: Int = 0,
    private val encode: Encode,
    private val audioRecord: AudioRecord,
    private val acousticEchoCanceler: AcousticEchoCanceler?,
    private val automaticGainControl: AutomaticGainControl?,
    private val noiseSuppressor: NoiseSuppressor?,
) {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val recordData = MutableSharedFlow<ShortArray>(extraBufferCapacity = Int.MAX_VALUE)
    private val encoder = Encoder.createInstance(encode, getOutPath(), sampleRateInHz, channel.channelCount, 320_000)
    val volume = MutableStateFlow(0)
    var state: RecordState = RecordState.INIT
        private set

    fun startRecording() {
        if (audioRecord.state == AudioRecord.STATE_UNINITIALIZED) return
        if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) return
        if (state != RecordState.INIT) return
        calculateVolume()
        writeRecordData()
        recodeInner()
        state = RecordState.RECORDING
    }

    fun stopRecording() {
        if (state != RecordState.RECORDING) return
        audioRecord.stop()
        state = RecordState.PAUSED
    }

    fun resume() {
        if (state != RecordState.PAUSED) return
        state = RecordState.RECORDING
        recodeInner()
    }

    fun release() {
        if (state == RecordState.RELEASE) return
        encoder.release()
        state = RecordState.RELEASE
        job.cancel()
        audioRecord.stop()
        audioRecord.release()
        acousticEchoCanceler?.release()
        automaticGainControl?.release()
        noiseSuppressor?.release()
    }

    private fun recodeInner() {
        scope.launch {
            audioRecord.startRecording()
            while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING && isActive) {
                val buffer = ShortArray(minBufferSize)
                audioRecord.read(buffer, 0, minBufferSize)
                recordData.emit(buffer)
            }
        }
    }

    /**
     * 参考： https://www.cnblogs.com/renhui/p/11704635.html
     */
    private fun calculateVolume() {
        scope.launch {
            recordData.collect { array ->
                val db = 10 * log10(array.calculateRMS())
                volume.emit(db.toInt())
            }
        }
    }

    private fun writeRecordData() {
        scope.launch(Dispatchers.IO) {
            recordData.collect { buffer ->
                encoder.encodeChunk(buffer)
            }
        }
    }

    private fun getOutPath(): String {
        val suffix = if (encode == Encode.MP3) "mp3" else "m4a"
        val outFile =
            File(BaseApp.instance.getExternalFilesDir(suffix), System.currentTimeMillis().toString() + ".$suffix")
        return outFile.absolutePath
    }

    private fun ShortArray.calculateRMS(): Double {
        var sum = 0.0
        for (sample in this) {
            sum += sample * sample
        }
        val mean = sum / this.size
        return mean
    }

    class Builder {
        private var minBufferSize: Int = 0
        private var sampleRateInHz: Int = 44100
        private var audioFormat = AudioRecorderFormat.PCM_16BIT
        private var channelConfig = Channel.STEREO
        private var audioSource: AudioRecorderSource = AudioRecorderSource.MIC
        private var acousticEchoCanceler: AcousticEchoCanceler? = null
        private var automaticGainControl: AutomaticGainControl? = null
        private var noiseSuppressor: NoiseSuppressor? = null
        private var addAcousticEchoCanceler: Boolean = true
        private var addAutomaticGainControl: Boolean = true
        private var addNoiseSuppressor: Boolean = true
        private var encode = Encode.MP3


        @OptIn(DelicateCoroutinesApi::class)
        private var scope: CoroutineScope = GlobalScope

        /**
         * 设置音频来源
         */
        fun setAudioSource(audioSource: AudioRecorderSource): Builder {
            this.audioSource = audioSource
            return this
        }

        /**
         * 设置录音的协程域
         */
        fun setScope(scope: CoroutineScope): Builder {
            this.scope = scope
            return this
        }

        /**
         * 设置录音的编码方式
         */
        fun setEncode(encode: Encode): Builder {
            this.encode = encode
            return this
        }

        /**
         * 设置采样率
         */
        fun setSampleRateInHz(sampleRateInHz: Int): Builder {
            this.sampleRateInHz = sampleRateInHz
            return this
        }

        /**
         * 设置音频格式
         */
        fun setAudioFormat(audioFormat: AudioRecorderFormat): Builder {
            this.audioFormat = audioFormat
            return this
        }

        /**
         * 设置声道配置
         */
        fun setChannelConfig(channelConfig: Channel): Builder {
            this.channelConfig = channelConfig
            return this
        }

        /**
         * 禁用/启用声学回声消除器
         */
        fun isEnableAcousticEchoCanceler(value: Boolean): Builder {
            addAcousticEchoCanceler = value
            return this
        }

        /**
         * 禁用/启用自动增益控制
         */
        fun isEnableAutomaticGainControl(value: Boolean): Builder {
            addAutomaticGainControl = value
            return this
        }

        /**
         * 禁用/启用噪音抑制器
         */
        fun isEnableNoiseSuppressor(value: Boolean): Builder {
            addNoiseSuppressor = value
            return this
        }

        @SuppressLint("MissingPermission")
        fun build(): AudioRecorder {
            minBufferSize =
                AudioRecord.getMinBufferSize(
                    sampleRateInHz,
                    channelConfig.value,
                    audioFormat.value,
                )
            val audioRecord =
                AudioRecord(
                    audioSource.value,
                    sampleRateInHz,
                    channelConfig.value,
                    audioFormat.value,
                    minBufferSize,
                ).apply {
                    handleAcousticEchoCancel(audioSessionId)
                    handleAutomaticGainControl(audioSessionId)
                    handleNoiseSuppress(audioSessionId)
                }
            return AudioRecorder(
                sampleRateInHz,
                channelConfig,
                minBufferSize,
                encode,
                audioRecord,
                acousticEchoCanceler,
                automaticGainControl,
                noiseSuppressor,
            )
        }

        private fun handleAcousticEchoCancel(audioSessionId: Int) {
            if (!addAcousticEchoCanceler) {
                return
            }
            if (!AcousticEchoCanceler.isAvailable()) {
                return
            }
            acousticEchoCanceler = AcousticEchoCanceler.create(audioSessionId)
            acousticEchoCanceler?.enabled = true
        }

        private fun handleAutomaticGainControl(audioSessionId: Int) {
            if (!addAutomaticGainControl) {
                return
            }
            if (!AutomaticGainControl.isAvailable()) {
                return
            }
            automaticGainControl = AutomaticGainControl.create(audioSessionId)
            automaticGainControl?.enabled = true
        }

        private fun handleNoiseSuppress(audioSessionId: Int) {
            if (!addNoiseSuppressor) {
                return
            }
            if (!NoiseSuppressor.isAvailable()) {
                return
            }
            noiseSuppressor = NoiseSuppressor.create(audioSessionId)
            noiseSuppressor?.enabled = true
        }

        enum class AudioRecorderFormat(val value: Int) {
            PCM_16BIT(AudioFormat.ENCODING_PCM_16BIT),
        }

        enum class Channel(val value: Int, val channelCount: Int) {
            MOMO(AudioFormat.CHANNEL_IN_MONO, 1),
            STEREO(AudioFormat.CHANNEL_IN_STEREO, 2),
        }

        enum class AudioRecorderSource(val value: Int) {
            MIC(MediaRecorder.AudioSource.MIC),
        }
    }

    enum class RecordState {
        INIT,
        RECORDING,
        PAUSED,
        RELEASE,
    }

    enum class Encode {
        MP3, AAC_HW
    }
}