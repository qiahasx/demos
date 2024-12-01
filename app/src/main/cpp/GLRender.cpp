//
// Created by qiah on 2024/12/1.
//
#include <jni.h>
#include <string>
#include <GLES2/gl2.h>
#include <GLES/gl.h>
#include "log.h"

// 定义一个名为 PointF 的结构体，用于存储点的信息，包括坐标和颜色等属性
struct PointF {
    // 点的 x 坐标
    float x{};
    // 点的 y 坐标
    float y{};
    // 点的 z 坐标
    float z{};
    // 点的红色分量（颜色）
    float r{};
    // 点的绿色分量（颜色）
    float g{};
    // 点的蓝色分量（颜色）
    float b{};
    // 点的 alpha 分量（透明度），默认为 1.0
    float a = 1.0;
};

// 以下是一个 JNI 导出函数，用于初始化 OpenGL 环境
// env 是 JNI 环境指针，jobject 表示调用该函数的 Java 对象
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_GLRender_initOpenGL(
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

// 以下是一个 JNI 导出函数，用于执行绘制操作
// env 是 JNI 环境指针，jobject 表示调用该函数的 Java 对象
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_GLRender_draw(
        JNIEnv *env,
        jobject /* this */) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();
    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);
    PointF points[] = {
            {-0.5, -0.5, -1, 1.0, 0,   0},
            {0.5,  -0.5, -1, 0,   1.0, 0},
            {0.5, -0.1, -1, 0, 0, 0},
            {-0.5, -0.1, -1, 0, 0, 1.0},
    };
    glVertexPointer(3, GL_FLOAT, sizeof(PointF), points);
    glColorPointer(4, GL_FLOAT, sizeof(PointF), &points[0].r);
    // 设置线宽为 3
    glLineWidth(3);
    // 启用线平滑
    glEnable(GL_LINE_SMOOTH);
    // 设置线平滑的提示为最佳质量
    glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

    // 绘制一个闭合的线环，从数组的第 0 个元素开始，绘制 4 个顶点
    glDrawArrays(GL_LINE_LOOP, 0, 4);

    // 启用点平滑
    glEnable(GL_POINT_SMOOTH);
    // 设置点平滑的提示为最佳质量
    glHint(GL_POINT_SMOOTH, GL_NICEST);
    // 设置点的大小为 24
    glPointSize(24);
    // 绘制点，从数组的第 0 个元素开始，绘制 4 个顶点
    glDrawArrays(GL_POINTS, 0, 4);

    glDisableClientState(GL_COLOR_ARRAY);
    glDisableClientState(GL_VERTEX_ARRAY);
}

// 以下是一个 JNI 导出函数，用于处理 OpenGL 渲染器的大小调整操作
// env 是 JNI 环境指针，jobject 表示调用该函数的 Java 对象
// width 和 height 是从 Java 层传递过来的新的宽度和高度
extern "C" JNIEXPORT void JNICALL
Java_com_example_opengl_GLRender_resize(
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
    glOrthof(-1, 1, -1, 1, 0.1, 1000.0);
    // 将矩阵模式设置为模型视图矩阵
    glMatrixMode(GL_MODELVIEW);
    // 重置模型视图矩阵为单位矩阵
    glLoadIdentity();
}