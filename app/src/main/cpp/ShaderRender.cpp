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
#include "shader.h"
#include "Image.h"
#include <EGL/egl.h>

struct Vertex {
    glm::vec3 position;
    glm::vec2 texCoord;
};
const char *vertexShaderSrc = R"(#version 300 es
precision mediump float;

layout(location = 0) in vec3 aPosition;  // 顶点位置属性（location=0）
layout(location = 2) in vec2 aTexCoord;   // 纹理坐标属性

uniform mat4 uMVPMatrix;                // 模型视图投影矩阵
out vec2 vTexCoord;

void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 1.0); // 应用矩阵变换
     vTexCoord = aTexCoord;
}
)";
const char *fragmentShaderSrc = R"(#version 300 es
precision mediump float;

in vec2 vTexCoord;
out vec4 fragColor;        // 输出到帧缓冲的颜色
uniform sampler2D uTexture; // 纹理采样器

void main() {
    fragColor = texture(uTexture, vTexCoord); // 采样纹理
}
)";
// 顶点和索引数据
Vertex cubeVertices[] = {
        {{-0.3f, -0.5f, 0.5f},  {0.0f, 1.0f}},
        {{0.3f,  -0.5f, 0.5f},  {1.0f, 1.0f}},
        {{0.3f,  0.5f,  0.5f},  {1.0f, 0.0f}},
        {{-0.3f, 0.5f,  0.5f},  {0.0f, 0.0f}},

        {{-0.3f, -0.5f, -0.5f}, {1.0f, 1.0f}},
        {{0.3f,  -0.5f, -0.5f}, {0.0f, 1.0f}},
        {{0.3f,  0.5f,  -0.5f}, {0.0f, 0.0f}},
        {{-0.3f, 0.5f,  -0.5f}, {1.0f, 0.0f}},

        {{-0.3f, 0.5f,  0.5f},  {0.0f, 1.0f}},
        {{0.3f,  0.5f,  0.5f},  {1.0f, 1.0f}},
        {{0.3f,  0.5f,  -0.5f}, {1.0f, 0.0f}},
        {{-0.3f, 0.5f,  -0.5f}, {0.0f, 0.0f}},

        {{-0.3f, -0.5f, 0.5f},  {0.0f, 0.0f}},
        {{0.3f,  -0.5f, 0.5f},  {1.0f, 0.0f}},
        {{0.3f,  -0.5f, -0.5f}, {1.0f, 1.0f}},
        {{-0.3f, -0.5f, -0.5f}, {0.0f, 1.0f}},

        {{-0.3f, -0.5f, 0.5f},  {1.0f, 1.0f}},
        {{-0.3f, 0.5f,  0.5f},  {1.0f, 0.0f}},
        {{-0.3f, 0.5f,  -0.5f}, {0.0f, 0.0f}},
        {{-0.3f, -0.5f, -0.5f}, {0.0f, 1.0f}},

        {{0.3f,  -0.5f, 0.5f},  {0.0f, 1.0f}},
        {{0.3f,  0.5f,  0.5f},  {0.0f, 0.0f}},
        {{0.3f,  0.5f,  -0.5f}, {1.0f, 0.0f}},
        {{0.3f,  -0.5f, -0.5f}, {1.0f, 1.0f}}
};
GLushort cubeIndices[] = {
        0, 1, 2, 0, 2, 3,
        4, 5, 6, 4, 6, 7,
        8, 9, 10, 8, 10, 11,
        12, 13, 14, 12, 14, 15,
        16, 17, 18, 16, 18, 19,
        20, 21, 22, 20, 22, 23,
};

char *paths[] = {
        "/2.png",
        "/1.png",
        "/1.png",
        "/1.png",
        "/1.png",
        "/1.png"
};

class ShaderRender {
public:
    ShaderRender(const char *imagePath) {
        for (int i = 0; i < 6; ++i) {
            auto fullPath = std::string(imagePath).append(paths[i]);
            textureIds[i] = loadTexture(fullPath.c_str());
        }
        shaderProgram = createShaderProgram(vertexShaderSrc, fragmentShaderSrc);
        uMVPMatrixLocation = glGetUniformLocation(shaderProgram, "uMVPMatrix");
        uTextureLocation = glGetUniformLocation(shaderProgram, "uTexture");

        glGenVertexArrays(1, &vao);
        glGenBuffers(1, &vbo);
        glGenBuffers(1, &ebo);
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, sizeof(cubeVertices), cubeVertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(cubeIndices), cubeIndices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void *) nullptr);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex),
                              (void *) offsetof(Vertex, texCoord));
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);

        glClearColor(1.0, 1.0, 1.0, 1.0);
        glClearDepthf(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        view = glm::lookAt(glm::vec3(0.0f, 0.0f, 3.0f), glm::vec3(0.0f),
                           glm::vec3(0.0f, 1.0f, 0.0f));
    }

    void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(shaderProgram);
        mvp = projection * view * model;
        glUniformMatrix4fv(uMVPMatrixLocation, 1, GL_FALSE, glm::value_ptr(mvp));
        glBindVertexArray(vao);
        for (int i = 0; i < 6; ++i) {
            // 激活纹理单元并绑定纹理
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureIds[i]);
            glUniform1i(uTextureLocation, 0);

            // 绘制当前面（每面6个索引）
            auto *offset = (void *) (i * 6 * sizeof(GLushort));
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, offset);
        }

        glBindVertexArray(0);
    }

    void resize(int w, int h) {
        width = w;
        height = h;
        aspect = (float) width / (float) height;
        glViewport(0, 0, width, height);
        projection = glm::perspective(glm::radians(45.0f), aspect, 1.0f, 100.0f);
    }

    void rotate(float xAngle, float yAngle) {
        glm::quat qx = glm::angleAxis(glm::radians(xAngle), glm::vec3(0.0f, 1.0f, 0.0f));
        glm::quat qy = glm::angleAxis(glm::radians(yAngle), glm::vec3(1.0f, 0.0f, 0.0f));
        glm::quat q = qx * qy;
        model = glm::mat4_cast(q) * model;
    }

private:
    glm::mat4 model = glm::mat4(1.0f);
    glm::mat4 view;
    glm::mat4 projection;
    glm::mat4 mvp;
    GLuint textureIds[6];
    float aspect = 1.0f;
    int width, height;
    GLuint vao{}, vbo{}, ebo{};
    GLuint shaderProgram;
    GLuint uMVPMatrixLocation;
    GLuint uTextureLocation;
};

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_opengl_render_ShaderRender_initOpenGL(
        JNIEnv *env,
        jobject /* this */,
        jstring imagePath) {
    const char *path = env->GetStringUTFChars(imagePath, nullptr);
    auto *pRender = new ShaderRender(path);
    return (jlong) pRender;
}

auto angle1 = 0.0f;
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ShaderRender_draw(
        JNIEnv *env,
        jobject /* this */,
        jlong pRender) {
    reinterpret_cast<ShaderRender *>(pRender)->draw();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ShaderRender_resize(
        JNIEnv *env,
        jobject /* this */,
        jlong pRender,
        jint width,
        jint height) {
    reinterpret_cast<ShaderRender *>(pRender)->resize(width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_ShaderRender_rotate(
        JNIEnv *env,
        jobject /* this */,
        jlong pRender,
        jfloat xAngle,
        jfloat yAngle) {
    reinterpret_cast<ShaderRender *>(pRender)->rotate(xAngle, yAngle);
}