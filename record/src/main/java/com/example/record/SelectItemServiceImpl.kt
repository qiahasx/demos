package com.example.record

import androidx.annotation.Keep
import com.example.common.SelectItemService
import com.example.common.ui.ButtonItemBean
import com.example.common.util.startActivity

@Keep
class SelectItemServiceImpl : SelectItemService {
    override fun getSelectItem() = ButtonItemBean(R.string.recorder_demo, R.string.recorder_demo_info) { context, _ ->
        context.startActivity(RecordActivity::class.java)
    }
}