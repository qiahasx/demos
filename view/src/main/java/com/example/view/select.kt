package com.example.view

import com.example.common.ui.ButtonItemBean
import com.example.common.util.startActivity

val SELECT_VIEW_DEMO = listOf(
    ButtonItemBean(com.example.common.R.string.confirm, com.example.common.R.string.confirm) { context, _ ->
        context.startActivity(CenterActivity::class.java)
    },
    ButtonItemBean(com.example.common.R.string.confirm, com.example.common.R.string.confirm) { context, _ ->
        context.startActivity(ViewPageActivity::class.java)
    },
    ButtonItemBean(com.example.common.R.string.confirm, com.example.common.R.string.confirm) { context, _ ->
        context.startActivity(NestScrollActivity::class.java)
    }
)