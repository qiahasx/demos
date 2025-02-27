package com.example.launch.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.ui.ButtonItem
import com.example.common.ui.theme.AppTheme
import com.example.common.util.startActivity
import com.example.launch.SingleInstanceActivity
import com.example.launch.SingleTaskActivity
import com.example.launch.SingleTopActivity
import com.example.launch.StandardActivity

@Composable
fun LaunchLayout(modifier: Modifier = Modifier) {
    val activity = LocalActivity.current
    AppTheme {
        Scaffold { paddingValues ->
            Column(modifier.padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("$activity", Modifier.padding(16.dp), fontSize = 24.sp, fontWeight = FontWeight(700))
                }
                ButtonItem("StandardActivity") { _, _ -> activity.startActivity(StandardActivity::class.java) }
                ButtonItem("SingleTopActivity") { _, _ -> activity.startActivity(SingleTopActivity::class.java) }
                ButtonItem("SingleTaskActivity") { _, _ -> activity.startActivity(SingleTaskActivity::class.java) }
                ButtonItem("SingleInstanceActivity") { _, _ -> activity.startActivity(SingleInstanceActivity::class.java) }
            }
        }
    }
}

val LocalActivity = compositionLocalOf<ComponentActivity> { error("not init : LocalActivity") }