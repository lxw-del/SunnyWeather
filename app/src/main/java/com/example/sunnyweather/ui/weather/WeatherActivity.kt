package com.example.sunnyweather.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.ActivityWeatherBinding
import com.example.sunnyweather.databinding.ForecastBinding
import com.example.sunnyweather.databinding.LifeIndexBinding
import com.example.sunnyweather.databinding.NowBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import com.example.sunnyweather.setStatusBarTextColor
import com.example.sunnyweather.showToast
import com.example.sunnyweather.ui.place.WeatherViewModel
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    lateinit var bind:ActivityWeatherBinding

    lateinit var nowBind:NowBinding

    lateinit var forecastBind:ForecastBinding

    lateinit var lifeBind:LifeIndexBinding

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityWeatherBinding.inflate(layoutInflater)
        nowBind = NowBinding.inflate(layoutInflater)
        forecastBind = ForecastBinding.inflate(layoutInflater)
        lifeBind = LifeIndexBinding.inflate(layoutInflater)

        //让背景和状态栏融合到一起
        val window = this.window.apply {
            statusBarColor = Color.TRANSPARENT
        }
        setStatusBarTextColor(this,Color.TRANSPARENT)
        WindowCompat.setDecorFitsSystemWindows(window,false)

        setContentView(bind.root)

        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        viewModel.weatherLiveData.observe(this, Observer {
          val weather = it.getOrNull()
          if (weather != null){
              showWeatherInfo(weather)
          }else{
              "无法成功获取天气信息".showToast(this)
              it.exceptionOrNull()?.printStackTrace()
          }
        })
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
    }

    private fun showWeatherInfo(weather:Weather){
        nowBind.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now布局中的数据
        val currentTempText = "${realtime.temperature.toInt()}℃"
        nowBind.currentTemp.text = currentTempText
        nowBind.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        nowBind.currentAQI.text = currentPM25Text
        nowBind.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast布局中的数据
        forecastBind.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastBind.forecastLayout,false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val sktIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            //转换时间格式
            val simpleDateFormat = SimpleDateFormat("yy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            sktIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            //将赋值完的view，添加到预报布局界面。
            forecastBind.forecastLayout.addView(view)
        }

        //填充life_index布局中的数据
        val lifeIndex = daily.lifeIndex
        lifeBind.coldRiskText.text = lifeIndex.coldRisk[0].desc
        lifeBind.dressingText.text = lifeIndex.dressing[0].desc
        lifeBind.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        lifeBind.carWashText.text = lifeIndex.carWashing[0].desc
        bind.weatherLayout.visibility = View.VISIBLE
    }


}