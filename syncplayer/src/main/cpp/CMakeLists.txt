cmake_minimum_required(VERSION 3.22.1)

project("resample")
set(SOURCE_RESAMPLE libary/libresamplerate)
include_directories(${SOURCE_RESAMPLE}/include)
set(LIBRESAMPLE_SRCS
        ${SOURCE_RESAMPLE}/include/src_linear.c
        ${SOURCE_RESAMPLE}/include/src_sinc.c
        ${SOURCE_RESAMPLE}/include/src_zoh.c
        ${SOURCE_RESAMPLE}/include/samplerate.c
)

add_library(${CMAKE_PROJECT_NAME} SHARED
        jni.cpp
        ${LIBRESAMPLE_SRCS}
)

target_link_libraries(${CMAKE_PROJECT_NAME})

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99")
target_compile_definitions(${PROJECT_NAME} PRIVATE ENABLE_SINC_BEST_CONVERTER)
