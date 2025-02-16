package com.example.record

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import com.example.common.util.checkPermission
import com.example.common.util.toast
import com.example.record.ui.MainLayout

class RecordActivity : ComponentActivity() {
    private val viewModel by viewModels<RecordViewModel>()
    private lateinit var layout: MainLayout
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isgranted ->
            if (isgranted) {
                layout.btnAction.performClick()
            } else {
                toast("Permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(
            Intent(this, RecordService::class.java),
            object : ServiceConnection {
                override fun onServiceConnected(
                    name: ComponentName?,
                    service: IBinder?,
                ) {
                    viewModel.recordBinder = service as? RecordService.RecordBinder
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                }
            },
            Context.BIND_AUTO_CREATE,
        )
        setContentView(
            MainLayout(this).apply {
                layout = this
                btnAction.setOnClickListener {
                    if (checkPermission(Manifest.permission.RECORD_AUDIO)) {
                        viewModel.toggleRecord()
                        isSelected = viewModel.state == AudioRecorder.RecordState.RECORDING
                    } else {
                        requestPermission.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        )
    }

    fun navSetting(options: ActivityOptionsCompat) {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent, options.toBundle())
    }
}