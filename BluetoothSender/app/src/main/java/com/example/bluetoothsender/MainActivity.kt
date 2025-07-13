package com.example.bluetoothsender

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import android.net.Uri
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.os.PersistableBundle
import androidx.activity.compose.setContent
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

    @RequiresApi(Build.VERSION_CODES.S)
    private val bluetoothPermission=arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_SCAN
    )

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothService=bluetoothService(this)
//        compose状态保存
        var selectedUri by mutableStateOf<Uri?>(null)
        var fileContent by mutableStateOf<String?>(null)
//        文件选择器
        val filePickerLauncher=registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->selectedUri=uri
            fileContent=uri?.let{ TxtFile.readTextFromUri(this,it) }
        }

        val permissionLauncher=registerForActivityResult (
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permission->
            val denied=permission.filterValues { !it }.keys
            if (denied.isNotEmpty()){
                println("未授权的权限：$denied")
            }
        }
        permissionLauncher.launch(bluetoothPermission)

        setContent {
            BluetoothSenderTheme {
                MainScreen(
                    fileName = selectedUri?.let{ TxtFile.getFileName(this,it)},
                    onPickFile = {filePickerLauncher.launch("text/plain")},
                    bluetoothService=bluetoothService,
                    fileContent=fileContent?:"")
            }
        }
    }
}