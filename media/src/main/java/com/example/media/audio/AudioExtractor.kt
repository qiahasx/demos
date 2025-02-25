package com.example.media.audio

import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

class AudioExtractor(audioPath: String) {
    private val extractor = MediaExtractor()
    val format: MediaFormat
    val sampleTime
        get() = extractor.sampleTime

    init {
        extractor.setDataSource(audioPath)
        val track = findAudioTrack()
        extractor.selectTrack(track)
        format = extractor.getTrackFormat(track)
    }

    fun seekTo(timeUs: Long) {
        extractor.seekTo(timeUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
    }

    fun advance() {
        extractor.advance()
    }

    fun release() {
        extractor.release()
    }

    fun readSampleData(buffer: ByteBuffer, offset: Int) = extractor.readSampleData(buffer, offset)


    private fun findAudioTrack(): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val type = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (type.startsWith(AUDIO_TYPE)) return i
        }
        error("Not Found Audio Track")
    }

    companion object {
        private const val AUDIO_TYPE = "audio/"
    }
}