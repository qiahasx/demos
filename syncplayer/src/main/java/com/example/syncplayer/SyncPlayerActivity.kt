package com.example.syncplayer

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import com.example.syncplayer.ui.NavGraph
import com.example.syncplayer.ui.dialog.TextInfoDialogController
import com.example.syncplayer.util.launchMain
import com.example.syncplayer.viewModel.DialogManager
import com.example.syncplayer.viewModel.MainViewModel
import com.example.syncplayer.viewModel.MainViewModel.Companion.AUDIO_PATH
import com.example.syncplayer.viewModel.NavViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SyncPlayerActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private val dialogManager by viewModels<DialogManager>()
    private val navViewModel by viewModels<NavViewModel>()

    private val pickFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                launchMain {
                    val uri = it.data?.data ?: return@launchMain
                    val fileName = getFileNameFromUri(uri) ?: return@launchMain
                    val file = File(getExternalFilesDir(AUDIO_PATH), fileName)
                    file.outputStream().use { outputStream ->
                        contentResolver.openInputStream(uri)?.copyTo(outputStream)
                    }
                    viewModel.updateItem()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalPickFile provides pickFile,
                LocalMainViewModel provides viewModel,
                LocalDialogManager provides dialogManager,
                LocalNavViewModel provides navViewModel,
            ) {
                NavGraph()
            }
        }
        if (MMKV.defaultMMKV().getBoolean(KEY_SHOULD_GUIDE, true)) {
            dialogManager.show(
                TextInfoDialogController(
                    getString(R.string.sync_player_info),
                    getString(R.string.guide_title),
                    false
                )
            )
            MMKV.defaultMMKV().putBoolean(KEY_SHOULD_GUIDE, false)
        }
    }

    private suspend fun getFileNameFromUri(uri: Uri): String? =
        withContext(Dispatchers.IO) {
            var result: String? = null
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        result = it.getString(displayNameIndex)
                    }
                }
            }
            result
        }

    companion object {
        const val KEY_SHOULD_GUIDE = "should_guide"
    }
}
