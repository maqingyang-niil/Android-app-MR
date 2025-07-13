package com.example.bluetoothsender.bluetoothService

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.util.Log
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import java.io.IOException
import java.util.*


class bluetoothService(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter?by lazy{
        val bluetoothManager=context.getSystemService(Context.BLUETOOTH_SERVICE)as BluetoothManager
        bluetoothManager.adapter
    }
    private var socket: BluetoothSocket?=null
    private val SPP_UUID:UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getPairedDevice():List<BluetoothDevice>{
        return if(bluetoothAdapter?.isEnabled==true){
            bluetoothAdapter?.bondedDevices?.toList()?:emptyList()
        }else{
            emptyList()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    fun connectToDevice(device: BluetoothDevice): Boolean{
        return try{
            socket=device.createRfcommSocketToServiceRecord(SPP_UUID)

            bluetoothAdapter?.cancelDiscovery()
            socket?.connect()
            Log.d("BluetoothModule", "连接成功")
            true
        }catch(e:IOException){
            Log.e("BluetoothModule", "连接失败：${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun sendData(data:String): Boolean{
        return try{
            val outputStream=socket?.outputStream
            outputStream?.write(data.toByteArray())
            outputStream?.flush()
            Log.d("BluetoothModule", "数据发送成功")
            true
        }catch(e:IOException){
            Log.e("BluetoothModule", "发送失败：${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun disconnect(){
        try{
            socket?.close()
            socket=null
            Log.d("BluetoothModule", "已断开连接")
        }catch(e: IOException){
            Log.e("BluetoothModule", "断开失败：${e.message}")
        }
    }


}