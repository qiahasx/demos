package com.example.syncplayer.ui.dialog

import androidx.compose.runtime.Composable

abstract class DialogController {
    private var onDismissCallback: (() -> Unit)? = null

    fun setDismiss(callback: () -> Unit) {
        onDismissCallback = callback
    }

    fun dismiss() {
        onDismissCallback?.invoke()
    }

    @Composable
    abstract fun show()
}