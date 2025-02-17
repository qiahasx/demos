package com.example.syncplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncplayer.LocalDialogManager
import com.example.syncplayer.R
import com.example.syncplayer.audio.resample.KEY_USE_JNI_RESAMPLE
import com.example.syncplayer.audio.resample.getIsUseJniResample
import com.example.syncplayer.ui.dialog.TextInfoDialogController
import com.example.syncplayer.ui.theme.ComposeTheme
import com.example.syncplayer.util.getString
import com.tencent.mmkv.MMKV

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingLayout() {
    ComposeTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors =
                    topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(getString(R.string.setting))
                    },
                )
            },
        ) { paddingValues ->
            val context = LocalContext.current
            val dialogManager = LocalDialogManager.current
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                        .padding(12.dp, 4.dp)
                ) {
                    Row(Modifier.weight(1f)) {
                        Text(getString(R.string.use_JNI_resample), Modifier.align(Alignment.CenterVertically), fontSize = 16.sp)
                        IconButton({
                            dialogManager.show(
                                TextInfoDialogController(
                                    context.getString(R.string.jni_resample_content),
                                    context.getString(R.string.jni_resample_title)
                                )
                            )
                        }) {
                            Icon(Icons.Default.Info, "")
                        }
                    }
                    var isUseJni by remember { mutableStateOf(getIsUseJniResample()) }
                    Switch(isUseJni, {
                        MMKV.defaultMMKV().putBoolean(KEY_USE_JNI_RESAMPLE, it)
                        isUseJni = it
                    }, Modifier)
                }
            }
        }
    }
}