//
// Created by qiah on 2025/2/8.
//
#include <jni.h>
#include <string>
#include <GLES3/gl3.h>
#include "log.h"
#include "glm/vec4.hpp"
#include "glm/vec3.hpp"
#include "glm/mat4x4.hpp"
#include "glm/gtx/transform.hpp"
#include "glm/gtc/type_ptr.hpp"
#include <EGL/egl.h>

struct Vertex {
    glm::vec3 position;
    glm::vec4 color;
};
const char *vertexShaderSrc = R"(
#version 300 es
precision mediump float;

layout(location = 0) in vec3 aPosition;  // 顶点位置属性（location=0）
layout(location = 1) in vec4 aColor;     // 顶点颜色属性（location=1）

out vec4 vColor;                        // 传递给片元着色器的颜色
uniform mat4 uMVPMatrix;                // 模型视图投影矩阵

void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 1.0); // 应用矩阵变换
    vColor = aColor;                    // 直接传递顶点颜色
}
)";
const char *fragmentShaderSrc = R"(
#version 300 es
precision mediump float;

in vec4 vColor;            // 接收顶点着色器的颜色
out vec4 fragColor;        // 输出到帧缓冲的颜色

void main() {
    fragColor = vColor;    // 直接使用顶点颜色
}
)";

// 着色器程序和矩阵句柄
GLuint shaderProgram;
GLuint uMVPMatrixLocation;
EGLDisplay display;

// 顶点和索引数据
Vertex cubeVertices[] = {
        {{-0.5f, -0.5f, 0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
        {{0.5f,  -0.5f, 0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
        {{0.5f,  0.5f,  0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
        {{-0.5f, 0.5f,  0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},

        {{-0.5f, -0.5f, -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
        {{0.5f,  -0.5f, -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
        {{0.5f,  0.5f,  -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
        {{-0.5f, 0.5f,  -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},

        {{-0.5f, 0.5f,  0.5f},  {0.0f, 0.0f, 1.0f, 1.0f}},
        {{0.5f,  0.5f,  0.5f},  {0.0f, 0.0f, 1.0f, 1.0f}},
        {{0.5f,  0.5f,  -0.5f}, {0.0f, 0.0f, 1.0f, 1.0f}},
        {{-0.5f, 0.5f,  -0.5f}, {0.0f, 0.0f, 1.0f, 1.0f}},

        {{-0.5f, -0.5f, 0.5f},  {1.0f, 1.0f, 0.0f, 1.0f}},
        {{0.5f,  -0.5f, 0.5f},  {1.0f, 1.0f, 0.0f, 1.0f}},
        {{0.5f,  -0.5f, -0.5f}, {1.0f, 1.0f, 0.0f, 1.0f}},
        {{-0.5f, -0.5f, -0.5f}, {1.0f, 1.0f, 0.0f, 1.0f}},

        {{-0.5f, -0.5f, 0.5f},  {0.0f, 1.0f, 1.0f, 1.0f}},
        {{-0.5f, 0.5f,  0.5f},  {0.0f, 1.0f, 1.0f, 1.0f}},
        {{-0.5f, 0.5f,  -0.5f}, {0.0f, 1.0f, 1.0f, 1.0f}},
        {{-0.5f, -0.5f, -0.5f}, {0.0f, 1.0f, 1.0f, 1.0f}},

        {{0.5f,  -0.5f, 0.5f},  {1.0f, 0.0f, 1.0f, 1.0f}},
        {{0.5f,  0.5f,  0.5f},  {1.0f, 0.0f, 1.0f, 1.0f}},
        {{0.5f,  0.5f,  -0.5f}, {1.0f, 0.0f, 1.0f, 1.0f}},
        {{0.5f,  -0.5f, -0.5f}, {1.0f, 0.0f, 1.0f, 1.0f}}
};

GLushort cubeIndices[] = {
        0, 1, 2, 0, 2, 3,
        4, 5, 6, 4, 6, 7,
        8, 9, 10, 8, 10, 11,
        12, 13, 14, 12, 14, 15,
        16, 17, 18, 16, 18, 19,
        20, 21, 22, 20, 22, 23,
};
// VBO/EBO/VAO 句柄
GLuint vao, vbo, ebo;

GLuint compileShader(GLenum shaderType, const char *shaderSource) {
    GLuint shader = glCreateShader(shaderType);
    glShaderSource(shader, 1, &shaderSource, nullptr);
    glCompileShader(shader);
    GLint success;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    if (!success) {
        char infoLog[512];
        glGetShaderInfoLog(shader, 512, nullptr, infoLog);
        debug("Shader compilation failed: %s", infoLog);
    }
    return shader;
}

GLuint createShaderProgram(const char *vertexShaderSource, const char *fragmentShaderSource) {
    debug("111");
    GLuint program = glCreateProgram();
    debug("222");
    GLuint vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
    debug("333");
    GLuint fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
    debug("444");
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);
    glLinkProgram(program);
    debug("555");
    // 检查链接错误
    GLint success;
    glGetProgramiv(program, GL_LINK_STATUS, &success);
    if (!success) {
        char infoLog[512];
        glGetProgramInfoLog(program, 512, nullptr, infoLog);
        debug("Program linking failed: %s", infoLog);
    }
    return program;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ShaderRender_initOpenGL(
        JNIEnv *env,
        jobject /* this */) {
    const EGLint attrib_list[] = {
            // this specifically requests an Open GL ES 2 renderer
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            // (ommiting other configs regarding the color channels etc...
            EGL_NONE
    };

    EGLConfig config;
    EGLint num_configs;
    eglChooseConfig(display, attrib_list, &config, 1, &num_configs);

    const EGLint context_attrib_list[] = {
            // request a context using Open GL ES 2.0
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    EGLContext context = eglCreateContext(display, config, nullptr, context_attrib_list);
    const char *version = (const char *) glGetString(GL_VERSION);
    debug("OpenGL version: %s", version);
    debug("1");
    shaderProgram = createShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    debug("2");
    uMVPMatrixLocation = glGetUniformLocation(shaderProgram, "uMVPMatrix");
    debug("3");
    glGenVertexArrays(1, &vao);
    glGenBuffers(1, &vbo);
    glGenBuffers(1, &ebo);
    debug("4");
    glBindVertexArray(vao);

    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(cubeVertices), cubeVertices, GL_STATIC_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(cubeIndices), cubeIndices, GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void *) nullptr);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 4, GL_FLOAT, GL_FALSE, sizeof(Vertex),
                          (void *) offsetof(Vertex, color));
    glEnableVertexAttribArray(1);

    glBindVertexArray(0);
    debug("5");

    glClearColor(1.0, 1.0, 1.0, 1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
}

auto angle1 = 0.0f;
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ShaderRender_draw(
        JNIEnv *env,
        jobject /* this */) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    debug("6");

    glUseProgram(shaderProgram);
    debug("7");
    angle1 += 1;
    glm::mat4 model = glm::rotate(glm::mat4(1.0f), glm::radians(angle1),
                                  glm::vec3(1.0f, 1.0f, 1.0f));
    glm::mat4 view = glm::lookAt(glm::vec3(0.0f, 0.0f, 3.0f), glm::vec3(0.0f),
                                 glm::vec3(0.0f, 1.0f, 0.0f));
    glm::mat4 projection = glm::perspective(glm::radians(45.0f), 1.0f, 0.1f, 100.0f);
    glm::mat4 mvp = projection * view * model;
    debug("8");
    glUniformMatrix4fv(uMVPMatrixLocation, 1, GL_FALSE, glm::value_ptr(mvp));
    debug("9");
    glBindVertexArray(vao);
    glDrawElements(GL_TRIANGLES, sizeof(cubeIndices) / sizeof(GLushort), GL_UNSIGNED_SHORT,
                   nullptr);
    glBindVertexArray(0);
    debug("10");
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ShaderRender_resize(
        JNIEnv *env,
        jobject /* this */,
        jint width,
        jint height) {
    glViewport(0, 0, width, height);
}