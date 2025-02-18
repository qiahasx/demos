package com.example.record.encoder

import android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM
import com.example.media.audio.BaseChunkBufferProvider
import com.example.media.audio.ShortsInfo
import kotlinx.coroutines.channels.Channel

class AudioChunkDispatcher : BaseChunkBufferProvider() {
    private val channel = Channel<ShortsInfo>(Int.MAX_VALUE)

    fun send(shortsInfo: ShortsInfo) {
        channel.trySend(shortsInfo)
    }

    override suspend fun fetchNextRawChunk(): ShortsInfo? {
        return channel.receive().takeUnless { it.flags == BUFFER_FLAG_END_OF_STREAM }
    }

    override fun onChunkReleased() {
        channel.cancel()
    }
}