//
// Created by zhengmc01 on 2025/2/13.
//
#include <jni.h>
#include "TransitionRender.h"
#include "glm/vec3.hpp"
#include "glm/vec2.hpp"

struct Vertex {
    glm::vec3 position;
    glm::vec2 texCoord;
};

void TransitionRender::init() {
    loadShaderFromFiles("transition.vert", "transition.frag");
    addTextureFromFile("transition_0.png", GL_TEXTURE0);
    addTextureFromFile("transition_1.png", GL_TEXTURE1);
    glUseProgram(shaderProgram);
    progressLoc = glGetUniformLocation(shaderProgram, "progress");
    glUniform1i(glGetUniformLocation(shaderProgram, "oldTexture"), 0);
    glUniform1i(glGetUniformLocation(shaderProgram, "newTexture"), 1);
    glUniform1i(glGetUniformLocation(shaderProgram, "mode"), mode);
    vao.bind();
    std::vector<Vertex> vs = {
            {{-1.0f, -1.0f, 1.0f}, {0.0f, 1.0f}},
            {{1.0f,  -1.0f, 1.0f}, {1.0f, 1.0f}},
            {{1.0f,  1.0f,  1.0f}, {1.0f, 0.0f}},
            {{-1.0f, 1.0f,  1.0f}, {0.0f, 0.0f}},
    };
    vbo.bufferData(vs);
    vao.setAttribute<Vertex>(0, 3, GL_FLOAT, nullptr);
    vao.setAttribute<Vertex>(1, 2, GL_FLOAT, (void *) offsetof(Vertex, texCoord));
    std::vector<GLuint> indices = {0, 1, 2, 0, 2, 3};
    ebo.bufferData(indices);
    vao.unbind();
    vbo.unbind();
    ebo.unbind();
    glClearColor(1.0, 1.0, 1.0, 1.0);
}

void TransitionRender::draw() {
    glClear(GL_COLOR_BUFFER_BIT);
    progress = (progress + 0.003f);
    if (progress >= 1.0f) {
        progress = -0.2f;
        glUniform1f(progressLoc, 1.0f);
    } else if (progress >= 0.0f) {
        glUniform1f(progressLoc, progress);
    }
    vao.bind();
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, nullptr);
    vao.unbind();
}

void TransitionRender::resize(int width, int height) {
    glViewport(0, 0, width, height);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_opengl_render_TransitionRender_initOpenGL(
        JNIEnv *env,
        jobject thiz,
        jint mode) {
    auto *pRender = new TransitionRender(mode);
    return (jlong) pRender;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_TransitionRender_draw(
        JNIEnv *env,
        jobject /* this */,
        jlong pRender) {
    reinterpret_cast<TransitionRender *>(pRender)->draw();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_TransitionRender_resize(
        JNIEnv *env,
        jobject /* this */,
        jlong pRender,
        jint width,
        jint height) {
    reinterpret_cast<TransitionRender *>(pRender)->resize(width, height);
}