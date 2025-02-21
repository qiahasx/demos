package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common.MainViewModel
import com.example.common.ui.ButtonItem
import com.example.common.ui.ButtonItemBean
import com.example.common.ui.TextInfoDialog
import com.example.common.ui.theme.AppTheme
import com.example.common.util.LocalMainViewModel
import com.example.view.SELECT_VIEW_DEMO

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private val demos = listOf(
//        ButtonItemBean(R.string.opengl_demo, R.string.opengl_demo_info) { _, _ ->
//            viewModel.showBottomSheet(SELECT_OPENGL_DEMO)
//        },
//        ButtonItemBean(com.example.syncplayer.R.string.sync_player, com.example.syncplayer.R.string.sync_player_info) { _, _ ->
//            startActivity(SyncPlayerActivity::class.java)
//        },
//        ButtonItemBean(R.string.recorder_demo, R.string.recorder_demo_info) { _, _ ->
//            startActivity(RecordActivity::class.java)
//        },
        ButtonItemBean(R.string.recorder_demo, R.string.recorder_demo_info) { _, _ ->
            viewModel.showBottomSheet(SELECT_VIEW_DEMO)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
        onBackPressedDispatcher.addCallback {
            if (viewModel.bottomSheet.value != null) {
                viewModel.hideBottomSheet()
            } else {
                finish()
            }
        }
    }

    @Composable
    fun ButtonList(buttons: List<ButtonItemBean>) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =  Modifier.fillMaxSize()
        ) {
            items(buttons) { button ->
                ButtonItem(button)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun App() {
        val scaffoldState = rememberBottomSheetScaffoldState(rememberStandardBottomSheetState(confirmValueChange = {
            if (it == SheetValue.PartiallyExpanded && viewModel.bottomSheet.value != null) {
                viewModel.hideBottomSheet()
            }
            true
        }, skipHiddenState = false))
        val bottomSheetState = viewModel.bottomSheet.collectAsState()
        CompositionLocalProvider(
            LocalMainViewModel provides viewModel
        ) {
            AppTheme {
                // BottomSheetScaffold 疑是有bug 获得的padding一直为0, 暂时用Scaffold包裹一层
                Scaffold { padding ->
                    BottomSheetScaffold(
                        sheetContent = {
                            ButtonList(bottomSheetState.value?: emptyList())
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        scaffoldState = scaffoldState,
                        sheetPeekHeight = 0.dp,
                        sheetSwipeEnabled = false
                    ) {
                        ButtonList(demos)
                    }
                }

                LaunchedEffect(bottomSheetState.value) {
                    if (bottomSheetState.value == null) {
                        if (scaffoldState.bottomSheetState.isVisible) {
                            scaffoldState.bottomSheetState.hide()
                        }
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
                viewModel.dialog.collectAsState().value?.let { (title, message) ->
                    TextInfoDialog(title, message)
                }
            }
        }
    }

//    init {
//        System.loadLibrary("opengl")
//    }
}