package com.example.common.util

import androidx.compose.runtime.compositionLocalOf
import com.example.common.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow

val LocalDialog = compositionLocalOf<MutableStateFlow<Pair<String, String>?>> { error("not init : LocalDialog") }
val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("not init : LocalDialog") }