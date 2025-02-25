package com.example.common.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.common.R
import com.example.common.WebActivity.Companion.navWeb
import com.example.common.util.LocalMainViewModel
import com.example.common.util.getString
import java.util.regex.Pattern

@Composable
fun TextInfoDialog(
    title: String,
    message: String,
) {
    val mainViewModel = LocalMainViewModel.current
    AlertDialog(
        onDismissRequest = { mainViewModel.hideTextInfo() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            LinkText(parseUrl(message))
        },
        confirmButton = {
            OutlinedButton(
                onClick = { mainViewModel.hideTextInfo() },
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

@Composable
fun LinkText(inputText: MutableList<Pair<String, Boolean>>) {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        inputText.forEach { (text, isUrl) ->
            if (isUrl) {
                pushStringAnnotation(tag = "URL", annotation = text)
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(text)
                }
            } else {
                append(text)
            }
        }
    }
    ClickableText(
        text = annotatedString,
        style = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            color = Color.Black
        ),
        modifier = Modifier.padding(bottom = 8.dp),
        onClick = { offset ->
            val url = annotatedString
                .getStringAnnotations("URL", offset, offset)
                .lastOrNull() ?: return@ClickableText
            context.navWeb(url.item)
        }
    )
}

fun parseUrl(input: String): MutableList<Pair<String, Boolean>> {
    val pattern = Pattern.compile("http.*?#!")
    val matcher = pattern.matcher(input)
    val result = mutableListOf<Pair<String, Boolean>>()
    var lastEnd = 0
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        if (start > lastEnd) {
            result.add(Pair(input.substring(lastEnd, start), false))
        }
        result.add(Pair(input.substring(start, end - 2), true))
        lastEnd = end
    }
    if (lastEnd < input.length) {
        result.add(Pair(input.substring(lastEnd), false))
    }
    return result
}