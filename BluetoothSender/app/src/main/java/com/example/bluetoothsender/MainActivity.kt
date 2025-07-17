package com.example.bluetoothsender

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import android.net.Uri
import android.os.Bundle
import android.os.Build
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import com.example.bluetoothsender.bluetoothService.bluetoothService
import com.example.bluetoothsender.appui.MainScreen
import com.example.bluetoothsender.file.TxtFile
import com.example.bluetoothsender.ui.theme.BluetoothSenderTheme


@SuppressLint("RestrictedApi")
class MainActivity: ComponentActivity(){
    private lateinit var bluetoothService: bluetoothService
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private var hasBTPermission by mutableStateOf(false)


    private fun requestBTPermission(){
        when{
            Build.VERSION.SDK_INT>= Build.VERSION_CODES.S->{
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                    )
                )
            }else->{
                permissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        初始化bluetooth sender
        bluetoothService=bluetoothService(this)
//        初始化Launcher
        permissionLauncher=registerForActivityResult (
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permission->
            val denied=permission.filterValues { !it }.keys
            if (denied.isNotEmpty()){
                println("未授权的权限：$denied")
                hasBTPermission=false
            }else{
                hasBTPermission=true
            }
        }
         requestBTPermission()

//        compose状态保存
        var selectedUri by mutableStateOf<Uri?>(null)
        var fileContent by mutableStateOf<String?>(null)
//        文件选择器
        val filePickerLauncher=registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->selectedUri=uri
            fileContent=uri?.let{ TxtFile.readTextFromUri(this,it) }
        }

        setContent {
            BluetoothSenderTheme {
                MainScreen(
                    fileName = selectedUri?.let{ TxtFile.getFileName(this,it)},
                    onPickFile = {filePickerLauncher.launch("text/plain")},
                    bluetoothService=bluetoothService,
                    fileContent=fileContent?:"",
                    hasPermission = hasBTPermission)
            }
        }
    }
}