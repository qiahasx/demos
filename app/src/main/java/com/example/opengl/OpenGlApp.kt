package com.example.opengl

import android.app.Application
import com.tencent.mmkv.MMKV
import java.io.File

class OpenGlApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        if (MMKV.defaultMMKV().getBoolean(KEY_FIRST_START, true)) {
            copyAssets()
        }
    }

    private fun copyAssets() {
        val assetManager = assets
        val files = assetManager.list("image")
        files?.forEach { fileName ->
            assetManager.open("image/$fileName").use {
                debug(getExternalFilesDir("image")?.absolutePath.toString())
                val file = File(getExternalFilesDir("image"), fileName)
                it.copyTo(file.outputStream())
            }
        }
        MMKV.defaultMMKV().putBoolean(KEY_FIRST_START, false)
    }

    companion object {
        lateinit var instance: OpenGlApp
        const val KEY_FIRST_START = "first_start"
    }
}