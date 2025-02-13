
#include "Render.h"
#include "shader.h"
#include "Image.h"
#include <string>
#include <fstream>
#include <sstream>

std::string readFileContents(const std::string &fragPath);

void Render::loadShaderFromFiles(char *vertName, char fragName) {
    auto shaderPath = filePath.append("shader/");
    auto vertPath = shaderPath.append(vertName);
    auto fragPath = shaderPath.append(fragName);
    auto vertSrc = readFileContents(vertPath);
    auto fragSrc = readFileContents(fragPath);
    shaderProgram = createShaderProgram(vertSrc, fragSrc);
}

void Render::addTextureFromFile(char *imageName, GLenum target) {
    auto imagePath = filePath.append("image/").append(imageName);
    auto texture = loadTexture(imageName, target);
    textureIds.emplace_back(texture);
}


void Render::resize(int w, int h) {
    width = w;
    height = h;
    glViewport(0, 0, w, h);
}

std::string readFileContents(const std::string &fragPath) {
    std::ifstream file(fragPath);
    if (!file.is_open()) {
        throw std::runtime_error("无法打开文件: " + fragPath);
    }
    std::stringstream buffer;
    buffer << file.rdbuf();
    file.close();
    return buffer.str();
}
