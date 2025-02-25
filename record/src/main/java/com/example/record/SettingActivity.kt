package com.example.record

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.animation.AnticipateOvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.viewModels

class SettingActivity : ComponentActivity() {
    private val recordViewModel by viewModels<RecordViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(Intent(this, RecordService::class.java), object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val recordBinder = (service as? RecordService.RecordBinder) ?: return
                recordViewModel.recordBinder = recordBinder
                recordViewModel.volume.postValue(recordBinder.volume)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }, Context.BIND_AUTO_CREATE)
        val layout = SettingLayout(this)
        setContentView(layout)
        window.sharedElementEnterTransition.run {
            duration = 1000
            interpolator = AnticipateOvershootInterpolator()
        }
        lifecycle.addObserver(layout)
    }
}