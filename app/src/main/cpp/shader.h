//
// Created by zhengmc01 on 2025/2/10.
//

#include <GLES/gl.h>
#include <GLES3/gl3.h>
#include "log.h"

#ifndef OPENGL_SHADER_H
#define OPENGL_SHADER_H

#endif //OPENGL_SHADER_H

GLuint compileShader(GLenum shaderType, const char *shaderSource);

GLuint createShaderProgram(const char *vertexShaderSource, const char *fragmentShaderSource);