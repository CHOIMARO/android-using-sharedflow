package com.tngen

import android.app.Application
import android.content.Context
import com.tngen.flowtest.data.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FlowTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@FlowTestApplication)
            modules(appModule)
        }
    }
    override fun onTerminate() {
        super.onTerminate()
        appContext = null
    }
    companion object {
        var appContext: Context? = null
            private set
    }

}