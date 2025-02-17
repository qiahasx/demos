//
// Created by zhengmc01 on 2025/2/11.
//

#include <GLES3/gl3.h>

#ifndef OPENGL_ELEMENTRENDER_H
#define OPENGL_ELEMENTRENDER_H

#endif //OPENGL_ELEMENTRENDER_H

class ElementRender {
public:
    ElementRender(jint mode) : mode(mode) {
        init();
    }

    void draw() const;

    void resize(int width, int height);

private:
    void init();

    GLenum mode;
    GLuint vao, vbo;
    GLuint shaderProgram;
};