package com.example.common

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.common.util.getString
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

val Context.dataStore by preferencesDataStore(name = getString(R.string.app_name))
