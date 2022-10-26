package com.example.sunnyweather.ui.place

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sunnyweather.MainActivity
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.databinding.FragmentPlaceBinding
import com.example.sunnyweather.showToast
import com.example.sunnyweather.ui.weather.WeatherActivity

/**
 * 实现fragment的具体逻辑
 */
class PlaceFragment:Fragment() {
    //调用到的时候才会执行逻辑
    val viewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }

    private var _binding:FragmentPlaceBinding ?= null

    private val binding get() = _binding!! //要保证binding是一定为非空

    private lateinit var adapter:PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().lifecycle.addObserver(object : LifecycleEventObserver{
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.CREATED){

                    //判断当前是否已经有存储的城市信息，如果有直接跳转到该城市的天气界面
                    if(activity is MainActivity && viewModel.isPlaceSaved()){
                        val place = viewModel.getSavedPlace()

                        val intent = Intent(context, WeatherActivity::class.java).apply {
                            putExtra("location_lng", place.location.lng)
                            putExtra("location_lat", place.location.lat)
                            putExtra("place_name", place.name)
                        }

                        startActivity(intent)
                        activity?.finish()
                    }

                    //初始化RecyclerView
                    val layoutManager = LinearLayoutManager(activity)
                    binding.recyclerView.layoutManager = layoutManager
                    adapter = PlaceAdapter(this@PlaceFragment,viewModel.placeList)
                    binding.recyclerView.adapter = adapter

                    //
                    binding.searchPlaceEdit.addTextChangedListener{
                        val content = it.toString()
                        if(content.isNotEmpty()){
                            viewModel.searchPlaces(content)
                        }else{
                            binding.recyclerView.visibility = View.GONE
                            binding.bgImageView.visibility = View.VISIBLE
                            viewModel.placeList.clear()
                            adapter.notifyDataSetChanged()
                        }
                    }

                    viewModel.placeLiveData.observe(this@PlaceFragment, Observer {
                        val places = it.getOrNull()
                        if (places!= null){
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.bgImageView.visibility = View.GONE
                            viewModel.placeList.clear()
                            viewModel.placeList.addAll(places)
                            adapter.notifyDataSetChanged()
                        }else{
                            "未能查询到地址".showToast(SunnyWeatherApplication.context)
                            //Toast.makeText(activity,"asa",Toast.LENGTH_SHORT).show()
                            it.exceptionOrNull()?.printStackTrace()
                        }

                    })

                }

                lifecycle.removeObserver(this)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}