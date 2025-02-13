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
    glUseProgram(shaderProgram);
    auto error = glGetError();
}

void TransitionRender::draw() {
    glClear(GL_COLOR_BUFFER_BIT);
    vao.bind();
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textureIds[0]);
    auto loc = glGetUniformLocation(shaderProgram, "uTexture");
    glUniform1i(loc, 0);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    vao.unbind();
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_opengl_render_TransitionRender_initOpenGL(
        JNIEnv *env,
        jobject thiz,
        jint mode) {
    auto *pRender = new TransitionRender();
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

