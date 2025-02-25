package com.example.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.ui.ButtonItemBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _dialog = MutableStateFlow<Pair<String, String>?>(null)
    val dialog = _dialog.asStateFlow()
    private val _bottomSheet = MutableStateFlow<List<ButtonItemBean>?>(null)
    val bottomSheet = _bottomSheet.asStateFlow()

    fun showTextInfo(title: String, message: String) {
       viewModelScope.launch {
           _dialog.emit(Pair(title, message))
       }
    }

    fun hideTextInfo() {
        viewModelScope.launch {
            _dialog.emit(null)
        }
    }

    fun showBottomSheet(buttons: List<ButtonItemBean>) {
        viewModelScope.launch {
            _bottomSheet.emit(buttons)
        }
    }

    fun hideBottomSheet() {
        viewModelScope.launch {
            _bottomSheet.emit(null)
        }
    }
}