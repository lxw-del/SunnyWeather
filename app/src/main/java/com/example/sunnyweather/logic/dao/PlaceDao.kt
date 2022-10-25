package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

/**
 * 用以保存城市信息
 * 使用到了sharePreference
 */

object PlaceDao {

    //保存数据
    fun savePlace(place:Place){
        sharePreference().edit(){
            putString("place",Gson().toJson(place))
        }
    }
    //获得数据
    fun getSavedPlace():Place{
        val placeJson = sharePreference().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }
    //判断是否已经存储
    fun isPlaceSaved() = sharePreference().contains("place")

    //获取editor实例，sunny_weather是存储位置的名字
    private fun sharePreference() = SunnyWeatherApplication.context.
        getSharedPreferences("sunny_weather",Context.MODE_PRIVATE)

}