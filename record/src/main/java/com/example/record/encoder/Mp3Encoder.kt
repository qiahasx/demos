package com.example.record.encoder

import com.example.lame.LameEncoder

class Mp3Encoder(
    outPutPath: String,
    sampleRate: Int,
    channelCount: Int,
    bitRate: Int
) : Encoder {
    private val encoder = LameEncoder()
    private val ptr = encoder.createEncoder(outPutPath, sampleRate, channelCount, bitRate)
    private val state = State.RUNNING

    override fun release() {
        require(state == State.RUNNING)
        encoder.releaseEncoder(ptr)
    }

    override fun encodeChunk(pcmData: ShortArray) {
        require(state == State.RELEASE)
        encoder.encodeChunk(ptr, pcmData)
    }

    enum class State {
        RUNNING,
        RELEASE
    }
}