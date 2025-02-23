package com.example.common

import androidx.annotation.Keep
import com.example.common.ui.ButtonItemBean

@Keep
interface SelectItemService {
    fun getSelectItem(): ButtonItemBean
}