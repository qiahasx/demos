package com.example.opengl

import android.content.Context
import androidx.startup.Initializer
import java.io.File

class ResourceInitializer : Initializer<Unit> {
    init {
        System.loadLibrary("opengl")
    }

    override fun create(context: Context) {
       copyAssets(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    private fun copyAssets(context: Context) {
        val assetManager = context.assets
        val folders = listOf("image", "shader");
        folders.forEach { name ->
            val files = assetManager.list(name)
            files?.forEach { fileName ->
                assetManager.open("$name/$fileName").use {
                    val file = File(context.getExternalFilesDir(name), fileName)
                    it.copyTo(file.outputStream())
                }
            }
        }
    }
}