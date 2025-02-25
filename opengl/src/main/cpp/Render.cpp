
#include "Render.h"
#include "shader.h"
#include "Image.h"
#include <string>
#include <fstream>
#include <sstream>

std::string readFileContents(const std::string &fragPath);

void Render::loadShaderFromFiles(char *vertName, char *fragName) {
    auto shaderPath = filePath + "shader/";
    auto vertPath = shaderPath + vertName;
    auto fragPath = shaderPath + fragName;
    auto vertSrc = readFileContents(vertPath);
    auto fragSrc = readFileContents(fragPath);
    shaderProgram = createShaderProgram(vertSrc.c_str(), fragSrc.c_str());
}

GLint Render::addTextureFromFile(char *imageName, GLenum target) {
    auto imagePath = filePath + "image/" + imageName;
    auto texture = loadTexture(imagePath.c_str(), target);
    textureIds.emplace_back(texture);
    return texture;
}


void Render::resize(int w, int h) {
    width = w;
    height = h;
    glViewport(0, 0, w, h);
}

std::string readFileContents(const std::string &shaderPath) {
    std::ifstream file(shaderPath);
    if (!file.is_open()) {
        return nullptr;
    }
    std::stringstream buffer;
    buffer << file.rdbuf();
    file.close();
    return buffer.str();
}
