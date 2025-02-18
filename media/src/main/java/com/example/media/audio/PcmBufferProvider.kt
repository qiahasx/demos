package com.example.media.audio

interface PcmBufferProvider {
    suspend fun getBuffer(size: Int): ShortsInfo

    fun release()
}