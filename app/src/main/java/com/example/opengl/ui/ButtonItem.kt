package com.example.opengl.ui

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ButtonItem(
    @StringRes title: Int,
    @StringRes message: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val dialog = LocalDialog.current
    ElevatedButton(
        onClick = {
            onClick.invoke()
        },
        modifier = modifier
            .padding(16.dp)
            .height(44.dp)
    ) {
        Text(getString(title))
        IconButton({
            scope.launch(Dispatchers.IO) {
                dialog.emit(Pair(getString(title), getString(message)))
            }
        }) { Icon(Icons.Default.Info, "") }
    }
}


@Composable
fun ButtonItem(
    bean: ButtonItemBean
) {
    ButtonItem(bean.title, bean.message) { bean.onClick.invoke() }
}

data class ButtonItemBean(
    @StringRes val title: Int,
    @StringRes val message: Int,
    val onClick: () -> Unit,
)
