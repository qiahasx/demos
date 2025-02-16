cmake_minimum_required(VERSION 3.22.1)

project("mp3encoder")

add_library(
        MP3Encoder
        SHARED
        LameEncoder.cpp
        lame/reservoir.c
        lame/mpglib_interface.c
        lame/set_get.c
        lame/newmdct.c
        lame/id3tag.c
        lame/quantize.c
        lame/fft.c
        lame/gain_analysis.c
        lame/encoder.c
        lame/lame.c
        lame/bitstream.c
        lame/quantize_pvt.c
        lame/presets.c
        lame/psymodel.c
        lame/tables.c
        lame/takehiro.c
        lame/util.c
        lame/vbrquantize.c
        lame/VbrTag.c
        lame/version.c
)

target_link_libraries(MP3Encoder
android)
