package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName


/**
 * 定义沃我们的类与属性，也就是MVVM底层的model数据类
 */

data class PlaceResponse(val status: String,val places:List<Place>)

data class Place(val name:String,val location:Location,
                 @SerializedName("formatted_address") val address:String)
//用来存储位置对象
data class Location(val lng:String,val lat:String)