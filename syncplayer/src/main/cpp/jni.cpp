#include <jni.h>
#include <malloc.h>
#include <samplerate.h>
#include "common.h"

const float MAX_16_BIT = 32768.0f;

extern "C"
JNIEXPORT jshortArray JNICALL
Java_com_example_syncplayer_audio_resample_JniReSampler_resampleFromJNI(
        JNIEnv *env,
        jobject,
        jshortArray shorts,
        jint length,
        jlong re_sampler_pointer) {
    jshort *shortArrayElements = env->GetShortArrayElements(shorts, nullptr);
    if (shortArrayElements == nullptr) {
        return nullptr;
    }
    auto *srcState = reinterpret_cast<SRC_STATE *>(re_sampler_pointer);
    if (srcState == nullptr) {
        env->ReleaseShortArrayElements(shorts, shortArrayElements, 0);
        return nullptr;
    }
    auto *input = (float *) malloc(sizeof(float) * length);
    if (input == nullptr) {
        env->ReleaseShortArrayElements(shorts, shortArrayElements, 0);
        return nullptr;
    }
    for (int i = 0; i < length; ++i) {
        input[i] = (float) shortArrayElements[i] / MAX_16_BIT;
    }
    env->ReleaseShortArrayElements(shorts, shortArrayElements, 0);
    SRC_DATA srcData;
    int channels = srcState->channels;
    srcData.data_in = input;
    srcData.input_frames = length / channels;
    srcData.src_ratio = srcState->last_ratio;
    long outLength = static_cast<long>(length * srcData.src_ratio) + 1;
    srcData.output_frames = outLength / channels;
    auto *output = (float *) malloc(sizeof(float) * outLength);
    if (output == nullptr) {
        free(input);
        return nullptr;
    }
    srcData.data_out = output;
    src_reset(srcState);
    int error = src_process(srcState, &srcData);
    if (error) {
        free(input);
        free(output);
        return nullptr;
    }
    jshortArray result = env->NewShortArray(srcData.output_frames * channels);
    if (result == nullptr) {
        free(input);
        free(output);
        return nullptr;
    }

    auto *shortOutput = (jshort *) malloc(sizeof(jshort) * srcData.output_frames * channels);
    if (shortOutput == nullptr) {
        free(input);
        free(output);
        return nullptr;
    }
    for (long i = 0; i < srcData.output_frames * channels; ++i) {
        float sample = output[i] * MAX_16_BIT;
        if (sample > 32767.0f) sample = 32767.0f;
        if (sample < -32768.0f) sample = -32768.0f;
        shortOutput[i] = static_cast<jshort>(sample);
    }
    env->SetShortArrayRegion(result, 0, srcData.output_frames * channels, shortOutput);
    free(input);
    free(output);
    free(shortOutput);
    return result;
}

extern "C"
JNIEXPORT jlong JNICALL Java_com_example_syncplayer_audio_resample_JniReSampler_initReSampler(
        JNIEnv *env,
        jobject,
        jint oldSampleRate,
        jint newSampleRate,
        jint channels
) {
    int error;
    auto srcState = src_new(SRC_LINEAR, channels, &error);
    if (error) {
        return 0;
    }
    src_set_ratio(srcState, newSampleRate * 1.0 / oldSampleRate);
    auto re_sampler_pointer = (jlong) srcState;
    return re_sampler_pointer;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_syncplayer_audio_resample_JniReSampler_releaseReSampler(JNIEnv *env, jobject thiz,
                                                                         jlong re_sampler_pointer) {
    src_delete((SRC_STATE *) re_sampler_pointer);
}