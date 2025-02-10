
cmake_minimum_required(VERSION 3.22.1)
project("opengl")
add_compile_definitions(GLM_ENABLE_EXPERIMENTAL)
add_compile_definitions(STB_IMAGE_IMPLEMENTATION)
SET(SOURCE_GLM libary/libglm)
SET(SOURCE_STB_IMAGE libary/libstbImage)
INCLUDE_DIRECTORIES(${SOURCE_GLM}/include)
INCLUDE_DIRECTORIES(${SOURCE_STB_IMAGE}/include)
add_library(${CMAKE_PROJECT_NAME} SHARED
        GLRender.cpp
        ShaderRender.cpp
        ImageRender.cpp
        Image.h
        log.h
)
target_link_libraries(${CMAKE_PROJECT_NAME}
        -lGLESv1_CM
        -lGLESv2
        -lGLESv3
        -lEGL
        log)