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
    // 清除颜色缓冲区和深度缓冲区
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    // 重置当前矩阵为单位矩阵
    glLoadIdentity();
    // 启用顶点数组
    glEnableClientState(GL_VERTEX_ARRAY);
    // 启用颜色数组
    glEnableClientState(GL_COLOR_ARRAY);

    // 定义一个 PointF 类型的数组，存储三个点的信息
    PointF points[] = {
            {-0.5, -0.5, -1, 1.0, 0,   0},
            {0.5,  -0.5, -1, 0,   1.0, 0},
            {-0.5, -0.1, -1, 0, 0, 1.0},
            {0.5,  -0.1, -1, 0, 0, 0},
    };
    // 指定顶点数组的指针，每个顶点包含 3 个浮点数，步长为 PointF 的大小
    glVertexPointer(3, GL_FLOAT, sizeof(PointF), points);
    // 指定颜色数组的指针，从 points 数组的第一个元素的 r 分量开始，每个颜色包含 4 个浮点数，步长为 PointF 的大小
    glColorPointer(4, GL_FLOAT, sizeof(PointF), &points[0].r);
    // 绘制三角形，使用 3 个顶点
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    // 定义一个 PointF 类型的数组，存储三个点的信息
    PointF points2[] = {
            {-0.5, 0.5, -1, 1.0, 0,   0},
            {0.5,  0.5, -1, 0,   1.0, 0},
            {-0.5, 0.1, -1, 0,   0,   1.0},
            {0.5,  0.1, -1, 0,   0,   0},
    };
    // 指定顶点数组的指针，每个顶点包含 3 个浮点数，步长为 PointF 的大小
    glVertexPointer(3, GL_FLOAT, sizeof(PointF), points2);
    // 指定颜色数组的指针，从 points2 数组的第一个元素的 r 分量开始，每个颜色包含 4 个浮点数，步长为 PointF 的大小
    glColorPointer(4, GL_FLOAT, sizeof(PointF), &points2[0].r);
    // 绘制三角形，使用 3 个顶点
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    // 禁用颜色数组
    glDisableClientState(GL_COLOR_ARRAY);
    // 禁用顶点数组
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