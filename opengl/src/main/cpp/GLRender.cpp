//
// Created by qiah on 2024/12/1.
//
#include <jni.h>
#include <string>
#include <GLES2/gl2.h>
#include <GLES/gl.h>
#include "log.h"
#include "glm/vec4.hpp"
#include "glm/vec3.hpp"
#include "glm/mat4x4.hpp"
#include "glm/gtx/transform.hpp"
#include "glm/gtc/type_ptr.hpp"


struct Vertex {
    glm::vec3 position;
    glm::vec4 color;
};

// 以下是一个 JNI 导出函数，用于初始化 OpenGL 环境
// env 是 JNI 环境指针，jobject 表示调用该函数的 Java 对象
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_GLRender_initOpenGL(
        JNIEnv *env,
        jobject /* this */) {
    // 设置清除颜色为白色（RGBA）
    glClearColor(1.0, 1.0, 1.0, 1.0);
    // 设置清除深度为 1.0
    glClearDepthf(1.0);
    // 启用深度测试
    glEnable(GL_DEPTH_TEST);
    // 设置深度测试函数为 GL_LEQUAL
    glDepthFunc(GL_LEQUAL);
}

auto angle = 0.0f;
// 以下是一个 JNI 导出函数，用于执行绘制操作
// env 是 JNI 环境指针，jobject 表示调用该函数的 Java 对象
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_GLRender_draw(
        JNIEnv *env,
        jobject /* this */) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();
    // 设置矩阵模式为投影矩阵
    glMatrixMode(GL_PROJECTION);
    // 再次重置投影矩阵为单位矩阵
    glLoadIdentity();
    // 启用背面剔除，只绘制正面
    glCullFace(GL_BACK);
    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);
    Vertex cubeVertices[] = {
            {{-0.5f, -0.5f, 0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
            {{0.5f,  -0.5f, 0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
            {{0.5f,  0.5f,  0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
            {{-0.5f, -0.5f, 0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
            {{0.5f,  0.5f,  0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},
            {{-0.5f, 0.5f,  0.5f},  {1.0f, 0.0f, 0.0f, 1.0f}},

            {{-0.5f, -0.5f, -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
            {{0.5f,  -0.5f, -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
            {{0.5f,  0.5f,  -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
            {{-0.5f, -0.5f, -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
            {{0.5f,  0.5f,  -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},
            {{-0.5f, 0.5f,  -0.5f}, {0.0f, 1.0f, 0.0f, 1.0f}},

            {{-0.5f, 0.5f,  0.5f},  {0.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  0.5f,  0.5f},  {0.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  0.5f,  -0.5f}, {0.0f, 0.0f, 1.0f, 1.0f}},
            {{-0.5f, 0.5f,  0.5f},  {0.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  0.5f,  -0.5f}, {0.0f, 0.0f, 1.0f, 1.0f}},
            {{-0.5f, 0.5f,  -0.5f}, {0.0f, 0.0f, 1.0f, 1.0f}},

            {{-0.5f, -0.5f, 0.5f},  {1.0f, 1.0f, 0.0f, 1.0f}},
            {{0.5f,  -0.5f, 0.5f},  {1.0f, 1.0f, 0.0f, 1.0f}},
            {{0.5f,  -0.5f, -0.5f}, {1.0f, 1.0f, 0.0f, 1.0f}},
            {{-0.5f, -0.5f, 0.5f},  {1.0f, 1.0f, 0.0f, 1.0f}},
            {{0.5f,  -0.5f, -0.5f}, {1.0f, 1.0f, 0.0f, 1.0f}},
            {{-0.5f, -0.5f, -0.5f}, {1.0f, 1.0f, 0.0f, 1.0f}},

            {{-0.5f, -0.5f, 0.5f},  {0.0f, 1.0f, 1.0f, 1.0f}},
            {{-0.5f, 0.5f,  0.5f},  {0.0f, 1.0f, 1.0f, 1.0f}},
            {{-0.5f, 0.5f,  -0.5f}, {0.0f, 1.0f, 1.0f, 1.0f}},
            {{-0.5f, -0.5f, 0.5f},  {0.0f, 1.0f, 1.0f, 1.0f}},
            {{-0.5f, 0.5f,  -0.5f}, {0.0f, 1.0f, 1.0f, 1.0f}},
            {{-0.5f, -0.5f, -0.5f}, {0.0f, 1.0f, 1.0f, 1.0f}},

            {{0.5f,  -0.5f, 0.5f},  {1.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  0.5f,  0.5f},  {1.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  0.5f,  -0.5f}, {1.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  -0.5f, 0.5f},  {1.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  0.5f,  -0.5f}, {1.0f, 0.0f, 1.0f, 1.0f}},
            {{0.5f,  -0.5f, -0.5f}, {1.0f, 0.0f, 1.0f, 1.0f}}
    };
    glVertexPointer(3, GL_FLOAT, sizeof(Vertex), cubeVertices);
    glColorPointer(4, GL_FLOAT, sizeof(Vertex), &cubeVertices[0].color);

    angle += 0.01;
    glm::mat4x4 cubeMat;
    // 定义一个平移矩阵，将物体沿 (0.0f, 0.3f, 0.5f) 平移
    glm::mat4x4 cubeTransMat = glm::translate(glm::mat4(1.0f), glm::vec3(0.0f, 0.3f, 0.5f));
    // 定义一个旋转矩阵，绕 (1.0f, 1.0f, 1.0f) 轴旋转 angle 角度
    glm::mat4x4 cubeRotMat = glm::rotate(glm::mat4(1.0f), angle, glm::vec3(1.0f, 1.0f, 1.0f));
    // 定义一个缩放矩阵，将物体在三个维度上缩放为 0.3 倍
    glm::mat4x4 cubeScaleMat = glm::scale(glm::mat4(1.0f), glm::vec3(0.3f, 0.3f, 0.3f));
    // 组合平移、旋转和缩放矩阵
    cubeMat = cubeTransMat * cubeRotMat * cubeScaleMat;
    // 加载组合后的矩阵
    glLoadMatrixf(glm::value_ptr(cubeMat));
    // 绘制三角形，从数组的第 0 个元素开始，绘制 36 个顶点
    glDrawArrays(GL_TRIANGLES, 0, 36);
    glDisableClientState(GL_COLOR_ARRAY);
    glDisableClientState(GL_VERTEX_ARRAY);
}

// 以下是一个 JNI 导出函数，用于处理 OpenGL 渲染器的大小调整操作
// env 是 JNI 环境指针，jobject 表示调用该函数的 Java 对象
// width 和 height 是从 Java 层传递过来的新的宽度和高度
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_render_GLRender_resize(
        JNIEnv *env,
        jobject /* this */,
        jint width,
        jint height) {
    // 设置视口的大小，根据传入的宽度和高度
    glViewport(0, 0, width, height);
    // 将矩阵模式设置为投影矩阵
    glMatrixMode(GL_PROJECTION);
    // 重置投影矩阵为单位矩阵
    glLoadIdentity();
    // 设置正交投影矩阵，定义可见区域的范围
    glOrthof(-1, 1, -1, 1, -0.1, 1000.0);
}