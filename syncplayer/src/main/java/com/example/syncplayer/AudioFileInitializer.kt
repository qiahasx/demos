package com.example.syncplayer

import android.content.Context
import androidx.startup.Initializer
import com.example.syncplayer.viewModel.MainViewModel
import java.io.File

class AudioFileInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val shouldCopy = context.getExternalFilesDir(MainViewModel.AUDIO_PATH)?.listFiles()?.isEmpty() ?: true
        if (shouldCopy) {
            copyAudios(context)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    private fun copyAudios(context: Context) {
        val assetManager = context.assets
        val name = MainViewModel.AUDIO_PATH
        val files = assetManager.list(name)
        files?.forEach { fileName ->
            assetManager.open("$name/$fileName").use {
                val file = File(context.getExternalFilesDir(name), fileName)
                it.copyTo(file.outputStream())
            }
        }
    }

    companion object {
        private const val KEY_FIRST_START = "syncPlayer_first_start"
    }
}