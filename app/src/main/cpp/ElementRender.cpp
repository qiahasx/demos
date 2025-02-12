//
// Created by zhengmc01 on 2025/2/11.
//
#include <jni.h>
#include "ElementRender.h"
#include "glm/vec3.hpp"
#include "glm/vec4.hpp"
#include "shader.h"

struct Vertex {
    glm::vec3 position;
    glm::vec4 color;
};

const char *vsSrc = R"(#version 300 es
precision mediump float;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
out vec4 color;

void main() {
    gl_PointSize = 10.0;
    gl_Position = vec4(aPosition, 1.0);
    color = aColor;
}
)";

const char *fsSrc = R"(#version 300 es
precision mediump float;

in vec4 color;
out vec4 fragColor;

void main() {
    fragColor = color;
}
)";

Vertex vs[] = {
        {{-0.3f, 0.5f,  0.5f}, {0.0f, 0.0f, 1.0f, 1.0f}},
        {{0.3f,  0.5f,  0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
        {{0.3f,  -0.5f, 0.5f}, {1.0f, 0.0f, 0.0f, 1.0f}},
        {{-0.3f, -0.5f, 0.5f}, {0.0f, 0.0f, 0.0f, 1.0f}},
};

void ElementRender::init() {
    shaderProgram = createShaderProgram(vsSrc, fsSrc);
    glGenVertexArrays(1, &vao);
    glGenBuffers(1, &vbo);
    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vs), vs, GL_STATIC_DRAW);
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void *) nullptr);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 4, GL_FLOAT, GL_FALSE, sizeof(Vertex),
                          (void *) offsetof(Vertex, color));
    glEnableVertexAttribArray(1);
    glBindVertexArray(0);
    glClearColor(1.0, 1.0, 1.0, 1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
    glLineWidth(10);
}

void ElementRender::draw() const {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glUseProgram(shaderProgram);
    glBindVertexArray(vao);
    glDrawArrays(mode, 0, sizeof(vs) / sizeof(Vertex));
    glBindVertexArray(0);
}

void ElementRender::resize(int width, int height) {
    glViewport(0, 0, width, height);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_opengl_render_ElementRender_initOpenGL(
        JNIEnv *env,
        jobject thiz,
        jint mode) {
    auto *pRender = new ElementRender(mode);
    return (jlong) pRender;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ElementRender_draw(
        JNIEnv *env,
        jobject /* this */,
        jlong pRender) {
    reinterpret_cast<ElementRender *>(pRender)->draw();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ElementRender_resize(
        JNIEnv *env,
        jobject /* this */,
        jlong pRender,
        jint width,
        jint height) {
    reinterpret_cast<ElementRender *>(pRender)->resize(width, height);
}

