package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 * Json格式对应的数据模型
 *
 * 把所有的Json数据模型类都定义再RealtimeResponse的内部，这样可以防止出现和其他接口的数据类型有同名冲突的情况。
 */

data class RealtimeResponse(val status:String,val result: Result){
    data class Result(val realtime:Realtime)

    data class Realtime(val skycon:String,val temperature:Float,
                        @SerializedName("air_quality")val airQuality:AirQuality)

    data class AirQuality(val aqi:AQI)

    data class AQI(val chn:Float)
}