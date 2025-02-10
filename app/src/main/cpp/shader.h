//
// Created by zhengmc01 on 2025/2/10.
//

#include <GLES/gl.h>
#include <GLES3/gl3.h>
#include "log.h"

#ifndef OPENGL_SHADER_H
#define OPENGL_SHADER_H

#endif //OPENGL_SHADER_H

static GLuint compileShader(GLenum shaderType, const char *shaderSource) {
    GLuint shader = glCreateShader(shaderType);
    glShaderSource(shader, 1, &shaderSource, nullptr);
    glCompileShader(shader);
    GLint success;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    if (!success) {
        char infoLog[512];
        glGetShaderInfoLog(shader, 512, nullptr, infoLog);
        debug("Shader compilation failed: %s", infoLog);
    }
    return shader;
}

static GLuint
createShaderProgram(const char *vertexShaderSource, const char *fragmentShaderSource) {
    GLuint program = glCreateProgram();
    GLuint vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
    GLuint fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);
    glLinkProgram(program);
    // 检查链接错误
    GLint success;
    glGetProgramiv(program, GL_LINK_STATUS, &success);
    if (!success) {
        char infoLog[512];
        glGetProgramInfoLog(program, 512, nullptr, infoLog);
        debug("Program linking failed: %s", infoLog);
    }
    return program;
}