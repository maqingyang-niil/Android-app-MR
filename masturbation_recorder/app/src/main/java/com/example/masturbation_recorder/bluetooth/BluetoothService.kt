package com.example.masturbation_recorder.bluetooth

import android.bluetooth.BluetoothAdapter           // 蓝牙适配器，用于控制蓝牙
import android.bluetooth.BluetoothDevice            // 表示一个远程蓝牙设备
import android.bluetooth.BluetoothSocket            // 表示与设备连接后的 socket 通信通道
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log                              // 打日志用
import androidx.core.app.ActivityCompat
import java.io.IOException                           // 捕捉 IO 错误
import java.io.InputStream                           // 用于读取蓝牙传输过来的数据
import java.util.*                                    // 包括 UUID（唯一标识符）

class BluetoothService(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    //    蓝牙连接
    fun connect(device: BluetoothDevice): Boolean {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter?.cancelDiscovery()
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            inputStream = bluetoothSocket?.inputStream
            Log.e("BluetoothService", "连接成功")
            return true
        } catch (e: IOException) {
            Log.e("BluetoothService", "连接失败:${e.message}")
            return false
        }
    }

    //    蓝牙断开连接
    fun disconnect(){
        try{
            inputStream?.close()
            bluetoothSocket?.close()

        }catch (e: IOException){
            Log.e("BluetoothService","关闭失败:${e.message}")
        }
    }
//    读取数据
    fun receiveData():String?{
        return try{
            val buffer=ByteArray(1024)
            val bytes=inputStream?.read(buffer)?:-1
            if (bytes>0){
                String(buffer,0,bytes)
            }else{
                null
            }

        }catch(e: IOException){
            Log.e("BluetoothService","读取失败:${e.message}")
            null
        }
    }
}
