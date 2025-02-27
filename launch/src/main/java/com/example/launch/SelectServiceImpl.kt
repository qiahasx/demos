package com.example.launch

import com.example.common.SelectItemService
import com.example.common.ui.ButtonItemBean
import com.example.common.util.startActivity

class SelectServiceImpl : SelectItemService {
    override fun getSelectItem() = ButtonItemBean(com.example.common.R.string.confirm, com.example.common.R.string.confirm) { context, _ ->
        context.startActivity(StandardActivity::class.java)
    }
}