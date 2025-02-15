package com.example.syncplayer.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun getString(@StringRes id: Int): String {
    val context = LocalContext.current
    return context.getString(id)
}