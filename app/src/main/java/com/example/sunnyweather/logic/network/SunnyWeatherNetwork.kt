package com.example.sunnyweather.logic.network

import android.telecom.Call
import retrofit2.Response
import java.lang.RuntimeException
import javax.security.auth.callback.Callback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 定义一个统一的网络数据源访问接口
 */

object SunnyWeatherNetwork {

    //获得动态代理对象实例
    private val placeService = ServiceCreator.create<PlaceService>()

    //发起搜索城市数据的请求，定义一个方法，await需要在挂机函数或者协程中执行，所以需要定义一个挂起函数去发起请求
    //并获取到服务器返回的数据
    suspend fun searchPlaces(query:String) = placeService.searchPlaces(query).await()

    //使用协程技术简化retrofit的回调
    private suspend fun <T> retrofit2.Call<T>.await():T{
        return suspendCoroutine {
            enqueue(object :retrofit2.Callback<T>{
                override fun onResponse(call: retrofit2.Call<T>, response: Response<T>) {
                    val body = response.body()
                    if(body != null) it.resume(body)
                    else it.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }
    }



}