package com.example.bluetoothsender.appui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bluetoothsender.bluetoothService.bluetoothService
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.nio.file.WatchEvent

@Composable
fun MainScreen(
    fileName: String?,
    onPickFile:()-> Unit,
    bluetoothService: bluetoothService,
    fileContent: String
    ){
    var pairedDevices by remember{mutableStateOf<List<BluetoothDevice>>(emptyList())}
    var sendingStatus by remember{mutableStateOf<String?>(null)}

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)){
        Text("蓝牙发送工具", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
//        选择文件
        Button(onClick = onPickFile, modifier = Modifier.fillMaxWidth()){
            Text("选择 .txt文件")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val context= LocalContext.current
        Button(
            onClick = {
                if(ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )== PackageManager.PERMISSION_GRANTED){
                    pairedDevices= bluetoothService.getPairedDevice()
                }else{
                    sendingStatus="请先授权蓝牙权限再尝试"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("获取配对设备")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("点击设备发送文件")
        pairedDevices.forEach { device->
            Text(
                text="${device.name}(${device.address})",
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable{
                        val success= bluetoothService.connectToDevice(device)
                        sendingStatus=if(success){
                            bluetoothService.sendData(fileContent)
                            "已发送到：${device.name}"
                        }else{
                            "连接失败：${device.name}"
                        }
                    }
            )
        }

        Spacer(modifier= Modifier.height(16.dp))

        sendingStatus?.let{
            Text(text=it, color = MaterialTheme.colorScheme.primary)
        }
    }
}