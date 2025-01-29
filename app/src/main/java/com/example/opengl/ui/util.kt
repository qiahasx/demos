package com.example.opengl.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun getString(@StringRes id: Int): String {
    val context = LocalContext.current
    return context.getString(id)
}

val LocalDialog = compositionLocalOf<MutableStateFlow<Pair<String, String>?>> { error("not init : LocalDialog") }