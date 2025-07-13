package com.example.masturbation_recorder


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.example.masturbation_recorder.bluetooth.BluetoothService
import androidx.annotation.RequiresPermission

class MainActivity : ComponentActivity(){
    private val REQUEST_CODE=100
    private lateinit var bluetoothService: BluetoothService




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE
        )
        bluetoothService = BluetoothService(this)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun getPairedDeviceAndConnect(){
        val pairedDevices:Set<BluetoothDevice>?=
            BluetoothAdapter.getDefaultAdapter()?.bondedDevices

        val targetDevice=pairedDevices?.find{it.name=="我的蓝牙模块名称"}

        if (targetDevice!=null){
            Thread {
                val connected = bluetoothService.connect(targetDevice)
                if (connected) {
                    while (true) {
                        val data = bluetoothService.receiveData()
                        if (data != null) {
                            Log.e("BluetoothData", "接收到:$data")
                        }
                    }
                } else {
                    Log.e("BluetoothError", "连接失败")
                }
            }.start()
        }else{
            Log.e("Bluetooth","找不到目标设备")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode==REQUEST_CODE){
            if (grantResults.all{it== PackageManager.PERMISSION_GRANTED }){
                getPairedDeviceAndConnect()
            }else{
                Log.e("Permission","权限被拒绝，无法访问蓝牙")
            }
        }
    }
}