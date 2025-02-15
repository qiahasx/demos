package com.example.syncplayer

import android.app.Application
import com.tencent.mmkv.MMKV

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        MMKV.initialize(this)
    }

    companion object {
        lateinit var context: App
    }
}
