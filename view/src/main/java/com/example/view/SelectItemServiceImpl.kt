package com.example.view

import androidx.annotation.Keep
import com.example.common.SelectItemService
import com.example.common.ui.ButtonItemBean


@Keep
class SelectItemServiceImpl : SelectItemService {
    override fun getSelectItem() =
        ButtonItemBean(R.string.view_demo, R.string.view_demo_info) { _, viewModel ->
            viewModel.showBottomSheet(SELECT_VIEW_DEMO)
        }
}