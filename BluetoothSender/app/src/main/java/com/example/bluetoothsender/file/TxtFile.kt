package com.example.bluetoothsender.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.BufferedReader
import java.io.InputStreamReader
import android.util.Log
import java.io.IOException
object TxtFile {
    fun readTextFromUri(context: Context,uri: Uri): String?{
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("TxtFileModule", "输入流为空，可能是 URI 无效：$uri")
                return null
            }

            val reader = BufferedReader(InputStreamReader(inputStream))
            val content = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                content.appendLine(line)
            }

            reader.close()
            inputStream.close()

            content.toString()

        } catch (e: Exception) {
            Log.e("TxtFileModule", "读取失败：${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun getFileName(context: Context,uri: Uri): String?{
        val cursor=context.contentResolver.query(uri,null,null,null,null)
        cursor?.use{
            if (it.moveToFirst()){
                return it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))

            }
        }
        return null
    }
}