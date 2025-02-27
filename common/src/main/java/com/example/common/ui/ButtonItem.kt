package com.example.common.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.common.MainViewModel
import com.example.common.util.LocalMainViewModel
import com.example.common.util.getString

@Composable
fun ButtonItem(
    title: String = "",
    message: String = "",
    onClick: (Context, MainViewModel) -> Unit,
) {
    val mainViewModel = LocalMainViewModel.current
    val context = LocalContext.current
    ElevatedButton(
        onClick = {
            onClick.invoke(context, mainViewModel)
        },
        modifier = Modifier
            .padding(16.dp)
            .height(44.dp)
    ) {
        Text(title)
        IconButton({
            mainViewModel.showTextInfo(title, message)
        }) { Icon(Icons.Default.Info, "") }
    }
}

@Composable
fun ButtonItem(
    @StringRes title: Int,
    @StringRes message: Int,
    onClick: (Context, MainViewModel) -> Unit,
) {
    ButtonItem(getString(title), getString(message), onClick)
}

@Composable
fun ButtonItem(
    bean: ButtonItemBean
) {
    ButtonItem(bean.title, bean.message, bean.onClick)
}

data class ButtonItemBean(
    @StringRes val title: Int,
    @StringRes val message: Int,
    val onClick: (Context, MainViewModel) -> Unit,
)
