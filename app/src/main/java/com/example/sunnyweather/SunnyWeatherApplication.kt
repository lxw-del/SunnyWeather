package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 用于存储全局的一些信息
 * 可以全局获取Context
 */
class SunnyWeatherApplication:Application() {

    companion object{

        const val TOKEN = "QZPIusVh7ZBQjCYe"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}