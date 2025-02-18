#include <jni.h>
#include "lame/lame.h"
#include <memory>
#include <stdexcept>

class LameEncoder {
private:
    FILE* outputFile = nullptr;
    lame_t lameClient = nullptr;
    int bufferSize = 1024 * 256;
    unsigned char* mp3Buffer;
    bool initialized = false;

public:
    LameEncoder( const char* outputPath,
                int sampleRate, int channels, int bitRate)
            : channelCount(channels) {
        outputFile = fopen(outputPath, "wb");
        if (!outputFile) {
            throw std::runtime_error("Failed to open output file");
        }
        lameClient = lame_init();
        lame_set_in_samplerate(lameClient, sampleRate);
        lame_set_out_samplerate(lameClient, sampleRate);
        lame_set_brate(lameClient, bitRate / 1000);
        lame_set_num_channels(lameClient, channels);
        lame_init_params(lameClient);
        mp3Buffer = new unsigned char[bufferSize];
        initialized = true;
    }

    ~LameEncoder() {
        if (lameClient) {
            int finalSize = lame_encode_flush(lameClient, mp3Buffer, bufferSize);
            if (finalSize > 0 && outputFile) {
                fwrite(mp3Buffer, 1, finalSize, outputFile);
            }
            lame_close(lameClient);
        }
        if (outputFile) fclose(outputFile);
        delete[] mp3Buffer;
    }

    bool encodeChunk(const short* pcmData, int sampleCount) {
        if (!outputFile || !lameClient) return false;

        int encodedSize = 0;
        if (channelCount == 1) {
            encodedSize = lame_encode_buffer(
                    lameClient,
                    pcmData,
                    nullptr,
                    sampleCount,
                    mp3Buffer,
                    bufferSize
            );
        } else {
            encodedSize = lame_encode_buffer_interleaved(
                    lameClient,
                    const_cast<short*>(pcmData),
                    sampleCount,
                    mp3Buffer,
                    bufferSize
            );
        }
        if (encodedSize < 0) return false;
        if (encodedSize > 0) {
            fwrite(mp3Buffer, 1, encodedSize, outputFile);
        }
        return true;
    }

    int channelCount;
};

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_lame_LameEncoder_createEncoder(
        JNIEnv* env, jobject thiz,
        jstring output_path,
        jint sample_rate, jint channels, jint bit_rate) {

    const char* outPath = env->GetStringUTFChars(output_path, nullptr);

    try {
        auto* encoder = new LameEncoder(outPath,sample_rate, channels, bit_rate);
        env->ReleaseStringUTFChars(output_path, outPath);
        return reinterpret_cast<jlong>(encoder);
    } catch (const std::exception& e) {
        env->ReleaseStringUTFChars(output_path, outPath);
        return 0;
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_lame_LameEncoder_encodeChunk(
        JNIEnv* env, jobject thiz, jlong encoder_ptr, jshortArray pcmData) {
    auto* encoder = reinterpret_cast<LameEncoder*>(encoder_ptr);
    if (!encoder) return JNI_FALSE;

    jsize length = env->GetArrayLength(pcmData);
    if (length == 0) return JNI_FALSE;

    jshort* data = env->GetShortArrayElements(pcmData, nullptr);
    bool success = encoder->encodeChunk(data, length / encoder->channelCount);
    env->ReleaseShortArrayElements(pcmData, data, JNI_ABORT);
    return success ? JNI_TRUE : JNI_FALSE;;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_lame_LameEncoder_releaseEncoder(
        JNIEnv* env, jobject thiz, jlong encoder_ptr) {
    auto* encoder = reinterpret_cast<LameEncoder*>(encoder_ptr);
    delete encoder;
}