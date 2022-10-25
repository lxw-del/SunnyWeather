package com.example.sunnyweather.logic.model

/**
 * 该类，用于将Realtime和Daily对象封装起来。
 */
data class Weather(val realtime: RealtimeResponse.Realtime,val daily: DailyResponse.Daily)