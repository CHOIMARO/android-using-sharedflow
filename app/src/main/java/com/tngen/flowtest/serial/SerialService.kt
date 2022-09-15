package com.tngen.flowtest.serial

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.tngen.FlowTestApplication
import com.tngen.flowtest.BuildConfig
import com.tngen.flowtest.uitl.SerialUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.lang.Exception

class SerialService : SerialInputOutputManager.Listener {
    private val INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".USB_PERMISSION"
    var port: UsbSerialPort? = null
    private val _sharedFlow = MutableSharedFlow<ByteArray>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun connect() {
        // Find all available drivers from attached devices.
        val manager = FlowTestApplication.appContext?.getSystemService(Context.USB_SERVICE) as UsbManager?
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }

        // Open a connection to the first available driver.
        val driver = availableDrivers[0]
        var connection: UsbDeviceConnection? = null
        if (manager!!.hasPermission(driver.device)) {
            connection = manager.openDevice(driver.device) ?: return
        }
        if (connection == null && !manager.hasPermission(driver.device)) {
            val usbPermissionIntent = PendingIntent.getBroadcast(
                FlowTestApplication.appContext,
                0,
                Intent(INTENT_ACTION_GRANT_USB),
                PendingIntent.FLAG_IMMUTABLE
            )
            if (manager.deviceList.size > 0) {
                manager.requestPermission(driver.device, usbPermissionIntent)
            }
        } else {
            port = driver.ports[0]
            port!!.open(connection)
            port!!.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

            val usbIoManager = SerialInputOutputManager(port, this)
            usbIoManager.start()

            Log.d("USBSerialService", "Connect Serial")
            port!!.write("Hello".toByteArray(), 2000)
        }
    }

    override fun onNewData(data: ByteArray?) {
        data!!.forEach {
//            SerialUtil.addDataString(String.format("%02X", it))
            SerialUtil.addDataString(it)
        }
        if(SerialUtil.isSerialReceived()) {
            GlobalScope.launch(Dispatchers.IO) {
                receivedMessage()
                SerialUtil.clearDataString()
            }

        }

    }
    suspend fun receivedMessage() = coroutineScope {
        _sharedFlow.emit(SerialUtil.dataByte)
    }


    override fun onRunError(e: Exception?) {
        Log.d("USBSerialService", "시리얼 연결 끊김")
    }
}