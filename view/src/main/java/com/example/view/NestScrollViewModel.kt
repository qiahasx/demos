package com.example.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NestScrollViewModel : ViewModel() {
    val selectTabIndex = MutableLiveData(0)
}