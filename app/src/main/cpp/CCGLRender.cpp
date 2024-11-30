//
// Created by qiah on 2024/12/1.
//
#include <jni.h>
#include <string>
#include <GLES2/gl2.h>
#include <GLES/gl.h>
#include "log.h"

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_CCGLRender_initOpenGL(
        JNIEnv *env,
        jobject /* this */) {
    glClearColor(1.0, 0, 0, 1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_CCGLRender_draw(
        JNIEnv *env,
        jobject /* this */) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_CCGLRender_resize(
        JNIEnv *env,
        jobject /* this */,
        jint width,
        jint height) {
    glViewport(0, 0, width, height);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrthof(-1, 1, -1, 1, 0.1, 1000.0);
}




