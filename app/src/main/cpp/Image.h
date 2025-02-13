#include <string.h>
#include <GLES3/gl3.h>
#include "stb_image.h"

class Image {
public:
    ~Image() {
        stbi_image_free(data);
    }

    static Image *CreateFormFile(const char *fileName) {
        if (fileName == nullptr) {
            return nullptr;
        }
        int type = 0;
        int width = 0;
        int height = 0;
        stbi_uc *imageData = stbi_load(fileName, &width, &height, &type, STBI_rgb_alpha);
        auto result = new Image(type, width, height, imageData);
        return result;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    stbi_uc *getData() {
        return data;
    }

private:
    Image(int t, int w, int h, stbi_uc *d) : type(t), width(w), height(h) {
        auto size = w * h * 4;
        if (size > 0 && d != nullptr) {
            data = d;
        }
    }

    int type{};
    int width{};
    int height{};
    stbi_uc *data = nullptr;
};

inline GLuint loadTexture(const char *filePath, GLenum target) {
    Image *image = Image::CreateFormFile(filePath);
    if (image->getData() == nullptr) {
        debug("Failed to load image from file: %s", filePath);
        return 0;
    }
    // 保存当前激活的纹理单元
    GLint originalActiveTexture;
    glGetIntegerv(GL_ACTIVE_TEXTURE, &originalActiveTexture);
    glActiveTexture(target);
    GLuint texture;
    glGenTextures(1, &texture);
    glBindTexture(GL_TEXTURE_2D, texture);

    // 设置纹理参数
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    // 上传纹理数据
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image->getWidth(), image->getHeight(), 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, image->getData());
    glGenerateMipmap(GL_TEXTURE_2D);

    glBindTexture(GL_TEXTURE_2D, texture);

    glActiveTexture(originalActiveTexture);
    delete image; // 释放图像数据

    return texture;
}