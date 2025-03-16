package com.example.demolist

import androidx.annotation.Keep
import com.example.common.SelectItemService
import com.example.common.ui.ButtonItemBean
import com.example.common.util.startActivity

@Keep
class SelectServiceImpl : SelectItemService {
    override fun getSelectItem() = ButtonItemBean(R.string.name, R.string.info) { context, _ ->
        context.startActivity(DemoSelectActivity::class.java)
    }
}