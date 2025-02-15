package com.example.common.util

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.common.BaseApp

fun getString(@StringRes id: Int): String {
    val context = BaseApp.instance
    return context.getString(id)
}

fun <T : Activity> Context.startActivity(tClass: Class<T>) {
    val intent = android.content.Intent(this, tClass)
    startActivity(intent)
}

