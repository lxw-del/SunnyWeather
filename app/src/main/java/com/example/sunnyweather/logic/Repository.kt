package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

/**
 * 仓库层的统一封装接口
 *  为了能将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个LiveData对象。
 */
object Repository {


    fun searchPlaces(query:String) = liveData(Dispatchers.IO){
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok"){
               val places = placeResponse.places
               Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e:Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}

/*livedata函数，可以自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文，这样我们就可以
在代码块中调用任意的挂起函数了。

emit方法其实类似于调用LiveData的setValue方法来通知数据变化，只不过这里我们无法直接取得返回的LiveData对象，所以
lifecycle-livedata-ktx库提供了一个替代方法

指定了Dispatchers.IO这样的代码块中的所有代码都运行在了子线程中。众所周知，Android不允许在主线程中进行网络请求的，
读写数据库之类的数据操作也不建议在主线程中进行的。所以需要进行一次线程转换
*/