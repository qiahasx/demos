package com.example.syncplayer.audio.resample

import com.example.syncplayer.audio.ShortsInfo

/**
 * 调用libsamplerate音频重采样 确实感觉比我自己写的线性插值转换效果好
 * 但是会导入杂音，猜测是因为buffer太小了
 * TODO: 尝试增大
 * github: https://github.com/libsndfile/libsamplerate
 */
class JniReSampler(
    oldSampleRate: Int,
    newSampleRate: Int,
    channelCount: Int,
) : ReSampler {
    private val reSamplerPointer: Long

    init {
        reSamplerPointer = initReSampler(oldSampleRate, newSampleRate, channelCount)
    }

    override fun reSampler(pcmData: ShortsInfo): ShortsInfo {
        val shorts = resampleFromJNI(pcmData.shorts, pcmData.size, reSamplerPointer) ?: return pcmData
        return ShortsInfo(shorts, 0, shorts.size, pcmData.sampleTime, pcmData.flags)
    }

    override fun release() {
        releaseReSampler(reSamplerPointer)
    }

    private external fun initReSampler(
        oldSampleRate: Int,
        newSampleRate: Int,
        channelCount: Int,
    ): Long

    private external fun releaseReSampler(reSamplerPointer: Long)

    private external fun resampleFromJNI(
        shorts: ShortArray,
        shortsSize: Int,
        reSamplerPointer: Long,
    ): ShortArray?

    companion object {
        init {
            System.loadLibrary("resample")
        }
    }
}