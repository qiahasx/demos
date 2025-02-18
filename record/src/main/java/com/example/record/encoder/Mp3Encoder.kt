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
    private var state = State.RUNNING

    override fun release() {
        require(state == State.RUNNING)
        encoder.releaseEncoder(ptr)
        state = State.RELEASE
    }

    override fun encodeChunk(pcmData: ShortArray) {
        require(state == State.RUNNING)
        encoder.encodeChunk(ptr, pcmData)
    }

    enum class State {
        RUNNING,
        RELEASE
    }
}