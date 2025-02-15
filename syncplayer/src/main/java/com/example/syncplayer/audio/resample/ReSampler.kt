package com.example.syncplayer.audio.resample

import com.example.syncplayer.audio.ShortsInfo
import com.tencent.mmkv.MMKV

interface ReSampler {
    fun reSampler(pcmData: ShortsInfo): ShortsInfo
    fun release() {}
}

const val KEY_USE_JNI_RESAMPLE = "use_jni_resampler"

fun getIsUseJniResample(): Boolean {
    return MMKV.defaultMMKV().getBoolean(KEY_USE_JNI_RESAMPLE, true)
}