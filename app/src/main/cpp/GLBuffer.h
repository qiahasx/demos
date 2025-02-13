#include <vector>
#include <string>
#include <stdexcept>
#include <type_traits>
#include <GLES3/gl3.h>
#include "log.h"

// 缓冲区类型
template<GLenum BufferType>
class GLBuffer {
public:
    GLBuffer() {
        glGenBuffers(1, &m_id);
        if (m_id == 0) {
            debug("%s", "Failed to create OpenGL buffer");
        }
    }

    ~GLBuffer() {
        if (m_id != 0) {
            glDeleteBuffers(1, &m_id);
        }
    }

    // 禁用拷贝
    GLBuffer(const GLBuffer &) = delete;

    GLBuffer &operator=(const GLBuffer &) = delete;

    // 启用移动语义
    GLBuffer(GLBuffer &&other) noexcept: m_id(other.m_id) {
        other.m_id = 0;
    }

    GLBuffer &operator=(GLBuffer &&other) noexcept {
        if (this != &other) {
            glDeleteBuffers(1, &m_id);
            m_id = other.m_id;
            other.m_id = 0;
        }
        return *this;
    }

    void bind() const {
        glBindBuffer(BufferType, m_id);
    }

    void unbind() {
        glBindBuffer(BufferType, 0);
    }

    template<typename T>
    void bufferData(const std::vector<T> &data, GLenum usage = GL_STATIC_DRAW) {
        static_assert(std::is_standard_layout_v<T>,
                      "Data must be standard layout");
        bind();
        glBufferData(BufferType,
                     data.size() * sizeof(T),
                     data.data(),
                     usage);
        checkGLError("Buffer data upload failed");
    }

    GLuint id() const noexcept { return m_id; }

private:
    GLuint m_id = 0;

    void checkGLError(const char *msg) {
        GLenum err = glGetError();
        if (err != GL_NO_ERROR) {
            debug("%s error code: %d", msg, err);
        }
    }
};

// 顶点数组对象 (VAO)
class VertexArray {
public:
    VertexArray() {
        glGenVertexArrays(1, &m_id);
        if (m_id == 0) {
            debug("%s", "Failed to create VAO");
        }
    }

    ~VertexArray() {
        if (m_id != 0) {
            glDeleteVertexArrays(1, &m_id);
        }
    }

    // 禁用拷贝
    VertexArray(const VertexArray &) = delete;

    VertexArray &operator=(const VertexArray &) = delete;

    // 移动语义
    VertexArray(VertexArray &&other) noexcept: m_id(other.m_id) {
        other.m_id = 0;
    }

    VertexArray &operator=(VertexArray &&other) noexcept {
        if (this != &other) {
            glDeleteVertexArrays(1, &m_id);
            m_id = other.m_id;
            other.m_id = 0;
        }
        return *this;
    }

    void bind() const {
        glBindVertexArray(m_id);
    }

    void unbind() {
        glBindVertexArray(0);
    }

    template<typename T>
    void setAttribute(GLuint index,
                      GLint size,
                      GLenum type,
                      const void *pointer,
                      GLboolean normalized = GL_FALSE) {
        static_assert(std::is_standard_layout_v<T>,
                      "Vertex attributes must use standard layout types");
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(
                index,
                size,
                type,
                normalized,
                sizeof(T),
                pointer
        );
        checkGLError("Vertex attribute setup failed");
    }

    GLuint id() const noexcept { return m_id; }

private:
    GLuint m_id = 0;

    void checkGLError(const char *msg) {
        GLenum err = glGetError();
        if (err != GL_NO_ERROR) {
            debug("%s error code: %d", msg, err);
        }
    }
};