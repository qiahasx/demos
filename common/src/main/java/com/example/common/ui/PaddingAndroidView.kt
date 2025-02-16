package com.example.common.ui

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun <T: View> PaddedAndroidView(factory: (Context) -> T) {
    Scaffold { paddings ->
        AndroidView(
            factory = { context ->
                factory.invoke(context).apply {
                    setPadding(paddings)
                }
            },
        )
    }
}

private fun View.setPadding(paddings: PaddingValues) {
    this.setPadding(
        paddings.calculateLeftPadding(LayoutDirection.Ltr).value.toInt(),
        paddings.calculateTopPadding().value.toInt(),
        paddings.calculateRightPadding(LayoutDirection.Ltr).value.toInt(),
        paddings.calculateBottomPadding().value.toInt(),
    )
}