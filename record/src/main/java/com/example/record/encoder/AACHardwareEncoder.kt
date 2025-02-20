package com.example.record.encoder

import com.example.media.audio.AACMediaCodecEncoder
import com.example.media.audio.ShortsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlin.math.roundToLong

class AACHardwareEncoder(
    outPutPath: String,
    sampleRate: Int,
    channelCount: Int,
    bitRate: Int,
) : Encoder {
    private val provider = AudioChunkProvider()

    @OptIn(DelicateCoroutinesApi::class)
    private val encoder = AACMediaCodecEncoder(outPutPath, sampleRate, channelCount, bitRate, GlobalScope)
    private var totalSize = 0.0
    private val pcmShortRate = sampleRate * channelCount

    init {
        encoder.setPcmData(provider)
        encoder.start()
    }

    override fun release() {
        provider.release()
    }

    override fun encodeChunk(pcmData: ShortArray) {
        totalSize += pcmData.size
        val shortsInfo = ShortsInfo(pcmData, 0, pcmData.size, getSampleTime(), 0)
        provider.send(shortsInfo)
    }

    private fun getSampleTime() = (totalSize * 1_000_000 / pcmShortRate).roundToLong()
}