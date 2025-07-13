package com.example.masturbation_recorder.bluetooth

import com.example.masturbation_recorder.bluetooth.DataType

object PacketParser {
    fun parseReceivedPacket(data: ByteArray):Pair<DataType, Any>?{
        if(data.size<7||data[0]!=0XAA.toByte()||data[1]!=0X55.toByte()) return null


    }
}