package com.example.common

import android.app.Application
import com.tencent.mmkv.MMKV

open class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
    }

    companion object {
        lateinit var instance: BaseApp
    }
}