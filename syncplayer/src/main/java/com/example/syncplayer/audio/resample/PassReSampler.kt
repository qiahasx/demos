package com.example.syncplayer.audio.resample

import com.example.media.audio.ShortsInfo

class PassReSampler : ReSampler {
    override fun reSampler(pcmData: ShortsInfo) = pcmData
}