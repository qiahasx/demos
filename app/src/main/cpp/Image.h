#include <string.h>
#include "stb_image.h"

//
// Created by qiah on 2025/1/11.
//
class Image {
public:
    Image(int t, int w, int h, char *d) : type(t), width(w), height(h) {
        auto size = w * h * 4;
        if (size > 0 && d != nullptr) {
            data = (char *) malloc(size);
            memcpy(data, d, size);
        }
    }

    ~Image() {
        free(data);
    }

    static Image *CreateFormFile(const char *fileName) {
        if (fileName == nullptr) {
            return nullptr;
        }
        int type = 0;
        int width = 0;
        int height = 0;
        stbi_uc *imageData = stbi_load(fileName, &width, &height, &type, STBI_rgb_alpha);
        auto result = new Image(type, width, height, (char *) imageData);
        stbi_image_free(imageData);
        return result;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    char *getData() {
        return data;
    }

private:
    int type{};
    int width{};
    int height{};
    char *data = nullptr;
};