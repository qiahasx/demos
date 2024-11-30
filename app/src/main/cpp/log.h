//
// Created by qiah on 2024/12/1.
//

#ifndef OPENGL_LOG_H
#define OPENGL_LOG_H

#ifdef __cplusplus
extern "C" {
#endif

#include <android/log.h>
#define LOG "NDK_OpenGLES"
#define debug(...) __android_log_print(ANDROID_LOG_DEBUG, LOG, __VA_ARGS__)
#ifdef __cplusplus
}
#endif
#endif //OPENGL_LOG_H
