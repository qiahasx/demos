//
// Created by qiah on 2025/1/11.
//
#include <jni.h>
#include <string>
#include <GLES/gl.h>
#include "log.h"
#include "glm/vec4.hpp"
#include "glm/vec3.hpp"
#include "glm/mat4x4.hpp"
#include "glm/gtx/transform.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "Image.h"

struct Vertex {
    glm::vec3 position;
    glm::vec2 texCoord;
};

class ImageRender {
public:
    ImageRender() : textureId(0) {
        glClearColor(0.0, 0.0, 0.0, 0.0);
        glClearDepthf(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
    }

    ~ImageRender() {
        if (textureId != 0) {
            glDeleteTextures(1, &textureId);
        }
    }

    void setImagePath(const char *path) {
        auto image = Image::CreateFormFile(path);
        textureId = createOpenGlTexture(image);
        delete image;
    }

    void draw() const {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // 绑定纹理
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureId);
        // 设置顶点、颜色和纹理坐标
        Vertex points[] = {
                {{-1, -1, 0}, {0.0f, 1.0f}}, // 左下角
                {{1,  -1, 0}, {1.0f, 1.0f}}, // 右下角
                {{1,  1,  0}, {1.0f, 0.0f}}, // 右上角
                {{-1, 1,  0}, {0.0f, 0.0f}} // 左上角
        };
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY); // 启用纹理坐标数组
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        glVertexPointer(3, GL_FLOAT, sizeof(Vertex), &points[0].position);
        glTexCoordPointer(2, GL_FLOAT, sizeof(Vertex), &points[0].texCoord); // 使用纹理坐标
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY); // 禁用纹理坐标数组
        glDisableClientState(GL_VERTEX_ARRAY);
    }

    void resize(int width, int height) {
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrthof(-1, 1, -1, 1, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

private:
    GLuint textureId;

    static GLint createOpenGlTexture(Image *image) {
        if (image == nullptr) return 0;
        GLuint id;
        glEnable(GL_TEXTURE_2D);
        glGenTextures(1, &id);
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image->getWidth(), image->getHeight(), 0, GL_RGBA,
                     GL_UNSIGNED_BYTE, image->getData());
        return id;
    }
};

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opengl_render_ImageGlRender_setImagePath(JNIEnv *env, jobject thiz, jstring path,
                                                   jlong renderPtr) {
    reinterpret_cast<ImageRender *>(renderPtr)->setImagePath(env->GetStringUTFChars(path, nullptr));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opengl_render_ImageGlRender_draw(JNIEnv *env, jobject thiz, jlong renderPtr) {
    reinterpret_cast<ImageRender *>(renderPtr)->draw();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_opengl_render_ImageGlRender_resize(JNIEnv *env, jobject thiz, jint width,
                                                    jint height,
                                             jlong renderPtr) {
    reinterpret_cast<ImageRender *>(renderPtr)->resize(width, height);
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_opengl_render_ImageGlRender_createImageRender(JNIEnv *env, jobject thiz) {
    auto *render = new ImageRender();
    return (jlong) render;
}