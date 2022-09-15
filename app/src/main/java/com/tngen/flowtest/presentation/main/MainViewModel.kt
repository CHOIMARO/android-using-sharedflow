package com.tngen.flowtest.presentation.main

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.tngen.flowtest.presentation.base.BaseViewModel
import com.tngen.flowtest.serial.SerialService
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val serialService: SerialService
) : BaseViewModel() {
    private val _sharedFlow = MutableSharedFlow<Int>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sharedFlow = _sharedFlow.asSharedFlow()

    override fun fetchData(): Job = viewModelScope.launch {
        serialService.connect()
        serialService.sharedFlow.collect() {
            receivedMessage()
        }

    }

    private fun receivedMessage() {
        Log.d("MainViewModel", "receivedMessage()")
        changeSharedFlow()
    }

    fun changeSharedFlow() = viewModelScope.launch {
        _sharedFlow.emit(10)
    }
}