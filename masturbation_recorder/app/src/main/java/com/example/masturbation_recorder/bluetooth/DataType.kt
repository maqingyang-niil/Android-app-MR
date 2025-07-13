package com.example.masturbation_recorder.bluetooth

enum class DataType(val code: Byte) {
    RAW_DATA(0X01),
    FFR_FREQ(0X02);

    companion object{
        fun fromByte(code: Byte): DataType?{
            return values().find{it.code==code}
        }
    }
}