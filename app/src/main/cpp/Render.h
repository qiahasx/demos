#include <GLES3/gl3.h>
#include <vector>
#include <string>

#ifndef OPENGL_RENDER_H
#define OPENGL_RENDER_H

#endif //OPENGL_RENDER_H

class Render {
public:
    virtual void init() = 0;

    virtual void draw() = 0;

    void resize(int w, int h);

    void loadShaderFromFiles(char *vertName, char fragName);

    void addTextureFromFile(char *imageName, GLenum target);

protected:
    int width, height;
    GLuint vao, vbo, ebo;
    GLuint shaderProgram;
    std::vector<GLint> textureIds{};
    std::string filePath = std::string(
            "/storage/emulated/0/Android/data/com.example.opengl/files/");
};