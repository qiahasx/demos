package com.example.opengl

import android.app.Application
import com.tencent.mmkv.MMKV
import java.io.File

class OpenGlApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        copyAssets()
    }

    private fun copyAssets() {
        val assetManager = assets
        val folders = listOf("image", "shader");
        folders.forEach { name ->
            val files = assetManager.list(name)
            files?.forEach { fileName ->
                assetManager.open("$name/$fileName").use {
                    val file = File(getExternalFilesDir(name), fileName)
                    it.copyTo(file.outputStream())
                }
            }
        }
    }

    companion object {
        lateinit var instance: OpenGlApp
        const val KEY_FIRST_START = "first_start"
    }
}