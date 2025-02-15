package com.example.demo

import com.example.common.BaseApp
import java.io.File

class DemoApplication : BaseApp() {
    override fun onCreate() {
        super.onCreate()
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
}