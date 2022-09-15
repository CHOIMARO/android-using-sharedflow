package com.tngen.flowtest.data.di

import com.tngen.flowtest.presentation.main.MainViewModel
import com.tngen.flowtest.serial.SerialService
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {
    single { Dispatchers.Main }
    single { Dispatchers.IO }

    viewModel { MainViewModel(get()) }

    single { SerialService() }
}