
cmake_minimum_required(VERSION 3.22.1)
project("opengl")

SET(SOURCE_GLM libary/libglm)

INCLUDE_DIRECTORIES(${SOURCE_GLM}/include)

add_library(${CMAKE_PROJECT_NAME} SHARED
        GLRender.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        -lGLESv1_CM
        -lGLESv2
        log)