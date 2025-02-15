package com.example.opengl.ui

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.example.opengl.OpenGlApp
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun getStringC(@StringRes id: Int): String {
    val context = LocalContext.current
    return context.getString(id)
}

fun getString(@StringRes id: Int): String {
    val context = OpenGlApp.instance
    return context.getString(id)
}

fun <T : Activity> Context.startActivity(tClass: Class<T>) {
    val intent = android.content.Intent(this, tClass)
    startActivity(intent)
}

val LocalDialog = compositionLocalOf<MutableStateFlow<Pair<String, String>?>> { error("not init : LocalDialog") }