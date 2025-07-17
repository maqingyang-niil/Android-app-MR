package com.example.bluetoothsender.appui


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bluetoothsender.bluetoothService.bluetoothService
import androidx.compose.ui.unit.dp
import android.widget.Toast

@Composable
fun MainScreen(
    fileName: String?,
    onPickFile:()-> Unit,
    bluetoothService: bluetoothService,
    fileContent: String,
    hasPermission: Boolean
    ){
    var pairedDevices by remember{mutableStateOf<List<BluetoothDevice>>(emptyList())}
    var sendingStatus by remember{mutableStateOf<String?>(null)}

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)){
        Text("蓝牙发送工具", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
//        选择文件
        Button(onClick = onPickFile, modifier = Modifier.fillMaxWidth()){
            Text(if (fileName.isNullOrEmpty()) "选择 .txt文件" else "重新选择")
        }



//        展示文件
        if (!fileName.isNullOrEmpty()){
            Spacer(modifier=Modifier.height(8.dp))
            Card(
                modifier= Modifier.fillMaxWidth(),
                colors= CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ){
                Column(modifier = Modifier.padding(12.dp)){
                    Text("已选择文件：",style=MaterialTheme.typography.bodyMedium)
                    Text(
                        text=fileName,
                        style=MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val context= LocalContext.current
//        选择设备
        Button(
            onClick = {
                when{
                    fileName.isNullOrEmpty()->{
                        Toast.makeText(context,"请先选择文件", Toast.LENGTH_SHORT).show()
                    }
                    !hasPermission->{
                        Toast.makeText(context,"请先授权蓝牙权限", Toast.LENGTH_SHORT).show()
                    }
                    !bluetoothService.isBluetoothEnabled()->{
                        Toast.makeText(context,"请先开启蓝牙", Toast.LENGTH_SHORT).show()
                    }else->{
                        @SuppressLint("MissingPermission")
                        pairedDevices= bluetoothService.getPairedDevice()
                        if(pairedDevices.isEmpty()){
                            Toast.makeText(context,"未找到配对设备", Toast.LENGTH_SHORT).show()
                        }else{
                            sendingStatus=null
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("获取配对设备")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("点击设备发送文件")
        pairedDevices.forEach { device->
            @SuppressLint("MissingPermission")
            Text(
                text="${device.name}(${device.address})",
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val success = bluetoothService.connectToDevice(device)
                        sendingStatus = if (success) {
                            bluetoothService.sendData(fileContent)
                            "已发送到：${device.name}"
                        } else {
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