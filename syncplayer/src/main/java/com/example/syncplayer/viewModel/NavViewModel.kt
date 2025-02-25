package com.example.syncplayer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.util.launchIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavViewModel : ViewModel() {
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun navPlay() {
        viewModelScope.launchIO {
            _navigationEvent.emit(NavigationEvent.NavigationPlay)
        }
    }

    fun navSettings() {
        viewModelScope.launchIO {
            _navigationEvent.emit(NavigationEvent.NavigationSetting)
        }
    }

    // 定义一个密封类来表示导航事件
    sealed class NavigationEvent {
        data object NavigationPlay : NavigationEvent()
        data object NavigationSetting : NavigationEvent()
    }
}