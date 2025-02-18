package com.example.record.encoder

import com.example.record.AudioRecorder

interface Encoder {
    fun release()

    fun encodeChunk(pcmData: ShortArray)

    companion object {
        fun createInstance(
            encode: AudioRecorder.Encode,
            outPutPath: String,
            sampleRate: Int,
            channelCount: Int,
            bitRate: Int,
        ): Encoder {
            return when (encode) {
                AudioRecorder.Encode.MP3 -> Mp3Encoder(outPutPath, sampleRate, channelCount, bitRate)
                AudioRecorder.Encode.AAC -> Mp3Encoder(outPutPath, sampleRate, channelCount, bitRate)
                AudioRecorder.Encode.AAC_HW -> AACHardwareEncoder(outPutPath, sampleRate, channelCount, bitRate)
            }
        }
    }
}