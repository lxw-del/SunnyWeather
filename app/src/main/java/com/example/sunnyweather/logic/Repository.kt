package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

/**
 * 仓库层的统一封装接口
 *  为了能将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个LiveData对象。
 */
object Repository {


    fun searchPlaces(query:String) = fire(Dispatchers.IO){
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok"){
               val places = placeResponse.places
               Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
    }

    //天气的Livedata封装对象
    fun refreshWeather(lng:String,lat:String) = fire(Dispatchers.IO) {
            coroutineScope {
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
                }

                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng,lat)
                }

                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()

                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                    val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                    Result.success(weather)
                }else{
                    Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}"+
                                   "daily time status is ${dailyResponse.status}" ))
                }
            }
    }

    //简化代码逻辑，不用每次写网络请求都写try catch，因为这里livedata也是一个泛型函数，且传入的泛型都是Result<T>的类型。
    private fun<T> fire(context: CoroutineContext,block:suspend ()->Result<T>)= liveData<Result<T>>(context){
        val result = try {
            block()
        }catch (e:Exception){
            Result.failure<T>(e)
        }
        emit(result)
    }

    //提供保存，和获取城市数据的接口
    //其实这里的实现方式并不标准，因为即使是对SharedPreferences文件进行读写的操作，也是不太建议在主线程进行
    //最佳的实现方式肯定是开一个线程来执行这些比较耗时的任务，然后通过LiveData对象进行数据返回。
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()


}

/*livedata函数，可以自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文，这样我们就可以
在代码块中调用任意的挂起函数了。

emit方法其实类似于调用LiveData的setValue方法来通知数据变化，只不过这里我们无法直接取得返回的LiveData对象，所以
lifecycle-livedata-ktx库提供了一个替代方法

指定了Dispatchers.IO这样的代码块中的所有代码都运行在了子线程中。众所周知，Android不允许在主线程中进行网络请求的，
读写数据库之类的数据操作也不建议在主线程中进行的。所以需要进行一次线程转换
*/