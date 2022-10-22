package com.example.sunnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place

/**
 *ViewModel层
 * 当searchLiveData的value发生变化，就会去调用switchMap将调用仓库层的网络请求，并且将它转换成可观察的LiveData
 */
class PlaceViewModel:ViewModel() {

    private val searchLiveData = MutableLiveData<String>()
    //用于对界面上显示的城市数据进行缓存，保证数据不会丢失。
    val placeList = ArrayList<Place>()

    //最终地址接口
    val placeLiveData = Transformations.switchMap(searchLiveData){
        Repository.searchPlaces(it)
    }
    //查询地址,你需要调用这个方法，才能有数据变化，一旦数据变化，switchMap就会被调用去转会LiveData
    fun searchPlaces(query:String){
        searchLiveData.value = query
    }
}