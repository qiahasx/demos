package com.example.syncplayer.ui.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.syncplayer.LocalDialogManager
import com.example.syncplayer.R
import com.example.syncplayer.ui.theme.ComposeTheme
import com.example.syncplayer.util.getString

class TextInfoDialogController(
    val message: String = "",
    val title: String? = null,
    val isCancel: Boolean = true,
) : DialogController() {
    @Composable
    override fun show() {
        TextInfoDialog(this)
    }
}

@Composable
fun TextInfoDialog(
    controller: TextInfoDialogController,
) {
    val dialogManager = LocalDialogManager.current
    controller.setDismiss { dialogManager.dismiss(controller) }
    ComposeTheme {
        AlertDialog(
            onDismissRequest = { if (controller.isCancel) controller.dismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            title = controller.title?.let {
                {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            text = {
                Text(
                    text = controller.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { controller.dismiss() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = getString(R.string.confirm),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }
}
