package com.example.media.audio

import android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM

abstract class BaseChunkBufferProvider : PcmBufferProvider {
    private var currentChunk: ShortsInfo? = null
    private var sampleTime = 0L

    protected abstract suspend fun fetchNextRawChunk(): ShortsInfo?
    protected abstract fun onChunkReleased()

    fun clearCache() {
        currentChunk = null
    }

    override suspend fun getBuffer(size: Int): ShortsInfo {
        val buffer = ShortArray(size)
        var bufferOffset = 0
        val sampleTime = sampleTime
        while (bufferOffset < size) {
            val chunk = getAvailableChunk() ?: break
            val remaining = size - bufferOffset
            val copySize = minOf(remaining, chunk.size)
            System.arraycopy(chunk.shorts, chunk.offset, buffer, bufferOffset, copySize)
            chunk.offset += copySize
            chunk.size -= copySize
            bufferOffset += copySize
            if (chunk.size <= 0) {
                currentChunk = null
            }
        }
        return ShortsInfo(
            buffer,
            size = bufferOffset,
            sampleTime = sampleTime,
            flags = if (bufferOffset < size) BUFFER_FLAG_END_OF_STREAM else 0
        )
    }

    private suspend fun getAvailableChunk(): ShortsInfo? {
        currentChunk?.takeIf { it.size > 0 }?.let { return it }
        currentChunk = fetchNextRawChunk()?.also {
            sampleTime = it.sampleTime
        }?.applyPreProcess()
        return currentChunk
    }

    protected open fun ShortsInfo?.applyPreProcess(): ShortsInfo? {
        return this
    }

    override fun release() {
        onChunkReleased()
    }
}
