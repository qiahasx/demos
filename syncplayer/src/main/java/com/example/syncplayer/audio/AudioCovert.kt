package com.example.syncplayer.audio

class AudioCovert(
    private val audioDecoder: AudioDecoder,
    private val bufferSize: Int,
) {
    private var targetSampleRate = getInputFormat().sampleRate
    private var targetChannels = if (audioDecoder.audioInfo.channelCount > 1) AudioTranscoder.Channels.Stereo
    else AudioTranscoder.Channels.Mono
    private val processor = PcmBufferProcessor(audioDecoder.queue)

    suspend fun getBuffer(size: Int = bufferSize) = processor.getBuffer(size)

    fun getInputFormat() = audioDecoder.audioInfo

    fun setTargetFormat(sampleRate: Int, channels: Int) {
        targetSampleRate = sampleRate
        targetChannels = if (channels > 1) AudioTranscoder.Channels.Stereo
        else AudioTranscoder.Channels.Mono
    }

    suspend fun seekTo(timeUs: Long) {
        clearCache()
        audioDecoder.seekTo(timeUs)
    }

    fun release() {
        audioDecoder.release()
        processor.release()
    }

    fun start() {
        processor.addReSampler(getInputFormat().channelCount, targetChannels.value, getInputFormat().sampleRate, targetSampleRate)
        audioDecoder.start()
    }

    private fun clearCache() = processor.clearCache()
}
