package com.example.opengl

import androidx.annotation.Keep
import com.example.common.SelectItemService
import com.example.common.ui.ButtonItemBean

@Keep
class SelectItemServiceImpl : SelectItemService {
    override fun getSelectItem() = ButtonItemBean(R.string.opengl_demo, R.string.opengl_demo_info) { _, viewModel ->
        viewModel.showBottomSheet(SELECT_OPENGL_DEMO)
    }
}