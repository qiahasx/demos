cmake_minimum_required(VERSION 3.22.1)

project("mp3encoder")

file(GLOB LAME_SOURCES "lame/*.c")

add_library(lame STATIC ${LAME_SOURCES})

add_library(MP3Encoder SHARED LameEncoder.cpp)

include_directories(lame)

target_link_libraries(MP3Encoder android lame)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DSTDC_HEADERS")
