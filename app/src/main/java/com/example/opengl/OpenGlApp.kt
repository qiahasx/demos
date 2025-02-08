package com.example.opengl

import android.app.Application
import com.tencent.mmkv.MMKV
import java.io.File

class OpenGlApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        if (MMKV.defaultMMKV().getBoolean(KEY_FIRST_START, true)) {
            copyAssets()
        }
    }

    private fun copyAssets() {
        val assetManager = assets
        val files = assetManager.list("image")
        files?.forEach { fileName ->
            assetManager.open(fileName).use {
                it.copyTo(File(getExternalFilesDir("image"), fileName).outputStream())
            }
        }
        MMKV.defaultMMKV().putBoolean(KEY_FIRST_START, false)
    }

    companion object {
        const val KEY_FIRST_START = "first_start"
    }
}