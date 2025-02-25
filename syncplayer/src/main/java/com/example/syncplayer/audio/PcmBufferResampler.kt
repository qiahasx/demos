package com.example.syncplayer.audio

import android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM
import com.example.media.audio.BaseChunkBufferProvider
import com.example.media.audio.ShortsInfo
import com.example.syncplayer.audio.resample.*

class PcmBufferResampler(
    private val pcmData: BlockQueue<ShortsInfo>,
    private val reSamplers: MutableList<ReSampler> = mutableListOf()
) : BaseChunkBufferProvider() {
    override suspend fun fetchNextRawChunk(): ShortsInfo? {
        return pcmData.consume().takeUnless { it.flags == BUFFER_FLAG_END_OF_STREAM }
    }

    fun addReSampler(
        inputChannels: Int,
        outputChannels: Int,
        inputSampleRate: Int,
        outputSampleRate: Int,
    ) {
        if (inputChannels > outputChannels) {
            addReSampler(StereoToMonoReSampler())
        }
        if (outputChannels < inputChannels) {
            addReSampler(MonoToStereoReSampler())
        }
        if (inputSampleRate != outputSampleRate) {
            if (getIsUseJniResample()) {
                addReSampler(JniReSampler(inputSampleRate, outputSampleRate, outputChannels))
            } else {
                addReSampler(LinearReSampler(inputSampleRate, outputSampleRate, outputChannels))
            }
        }
    }

    private fun addReSampler(reSampler: ReSampler) {
        reSamplers.add(reSampler)
    }

    override fun ShortsInfo?.applyPreProcess(): ShortsInfo? {
        return this?.let {
            reSamplers.fold(it) { chunk, reSampler ->
                reSampler.reSampler(chunk)
            }
        }
    }

    override fun onChunkReleased() {
        reSamplers.forEach { it.release() }
    }
}