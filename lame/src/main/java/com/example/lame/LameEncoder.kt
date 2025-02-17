package com.example.lame

class LameEncoder {
    external fun createEncoder(outPath: String, sampleRate: Int, channels: Int, bitRate: Int): Long
    external fun encodeChunk(ptr: Long, data: ShortArray): Boolean
    external fun releaseEncoder(ptr: Long)
}