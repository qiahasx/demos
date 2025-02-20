package com.example.media.audio

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import com.example.common.util.launchIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 使用MediaCodec将音频编码成aac格式
 */
class AACMediaCodecEncoder(
    outPutPath: String,
    private val sampleRate: Int,
    private val channelCount: Int,
    private val bitRate: Int,
    private val scope: CoroutineScope,
) {
    val progress = MutableStateFlow<Long>(0)
    private var state = State.Init
    private val codec = MediaCodec.createEncoderByType("audio/mp4a-latm")
    private val muxer = MediaMuxer(outPutPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    private var muxerTrackIndex = 0
    private val tempInfo = MediaCodec.BufferInfo()
    private var provider: PcmBufferProvider? = null
    private var isEndOfStreamReached = false
    private var isEndOfEncoded = false
    private val format = MediaFormat().apply {
        setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC)
        setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectMain)
        setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate)
        setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount)
        setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 256)
    }

    fun setPcmData(provider: PcmBufferProvider) {
        this.provider = provider
    }

    fun start() {
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        codec.start()
        scope.launchIO {
            while (!isEndOfEncoded) {
                submitPcmToCodec()
                processOutputBuffer()
            }
            release()
            progress.emit(EOF)
        }
        state = State.Running
    }

    private suspend fun processOutputBuffer() {
        when (val index = codec.dequeueOutputBuffer(tempInfo, 0)) {
            MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                muxerTrackIndex = muxer.addTrack(codec.outputFormat)
                muxer.start()
            }
            MediaCodec.INFO_TRY_AGAIN_LATER -> {}
            else -> {
                if (tempInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    isEndOfEncoded = true
                } else if (tempInfo.size > 0) {
                    val outputBuffer = codec.getOutputBuffer(index)!!
                    muxer.writeSampleData(muxerTrackIndex, outputBuffer, tempInfo)
                    codec.releaseOutputBuffer(index, false)
                    progress.emit(tempInfo.presentationTimeUs)
                }
            }
        }
    }

    private suspend fun submitPcmToCodec() {
        val processor = provider ?: error("Not Set PcmData")
        if (isEndOfStreamReached) return
        val index = codec.dequeueInputBuffer(0)
        if (index < 0) return
        val buffer = codec.getInputBuffer(index)?.asShortBuffer() ?: return
        val pcmShortInfo = processor.getBuffer(buffer.remaining())
        buffer.put(pcmShortInfo.shorts, pcmShortInfo.offset, pcmShortInfo.size)
        codec.queueInputBuffer(index, 0, buffer.position() * 2, pcmShortInfo.sampleTime, pcmShortInfo.flags)
        if (pcmShortInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
            isEndOfStreamReached = true
        }
    }

    private fun release() {
        if (state != State.Running) return
        provider?.release()
        codec.stop()
        codec.release()
        muxer.stop()
        muxer.release()
        state = State.End
    }

    companion object {
        const val EOF = -1L
    }

    private enum class State {
        Init, Running, End
    }
}