package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * 未来日期的天气预报
 *
 * tip：在数据模型中，可以使用List集合来对Json中数组元素进行映射
 */

data class DailyResponse(val status:String,val result: Result){
    data class Result(val daily:Daily)

    data class Daily(val temperature:List<Temperature>,val skycon:List<Skycon>
                    ,@SerializedName("life_index")val lifeIndex:LifeIndex)

    data class Temperature(val max:Float,val min:Float)

    data class Skycon(val value:String,val date: Date)

    data class LifeIndex(val coldRisk:List<LifeDescription>,val carWashing:List<LifeDescription>
                        ,val ultraviolet:List<LifeDescription>,val dressing:List<LifeDescription>)

    data class LifeDescription(val desc:String)
}