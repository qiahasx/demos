package com.example.syncplayer

import androidx.annotation.Keep
import com.example.common.SelectItemService
import com.example.common.ui.ButtonItemBean
import com.example.common.util.startActivity

@Keep
class SelectItemServiceImpl : SelectItemService {
    override fun getSelectItem() = ButtonItemBean(R.string.sync_player, R.string.sync_player_info) { context, _ ->
        context.startActivity(SyncPlayerActivity::class.java)
    }
}