package com.example.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import com.example.sunnyweather.setStatusBarVisible
import com.example.sunnyweather.showToast
import com.example.sunnyweather.ui.place.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    lateinit var weatherBind:ActivityWeatherBinding

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherBind = ActivityWeatherBinding.inflate(layoutInflater)

        //让背景和状态栏融合到一起
        val window = this.window.apply {
            statusBarColor = Color.TRANSPARENT
        }
        setStatusBarTextColor(this,Color.TRANSPARENT)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        setStatusBarVisible(this,false)
        setContentView(weatherBind.root)

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
            //获取刷新后的数据后，要将刷新按钮结束，并隐藏刷新进度条
            weatherBind.swipeRefresh.isRefreshing = false
        })
      //设置刷新按钮逻辑，设置颜色以及设置刷新监听事件，一旦刷新，则刷新天气。
      weatherBind.swipeRefresh.setColorSchemeResources(R.color.teal_200)
      refreshWeather()
      weatherBind.swipeRefresh.setOnRefreshListener {
          refreshWeather()
      }

      val navBtn = findViewById<Button>(R.id.navBtn)

      navBtn.setOnClickListener{
          weatherBind.drawerLayout.openDrawer(GravityCompat.START)
      }

      weatherBind.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{

          override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
          }

          override fun onDrawerOpened(drawerView: View) {
          }

          override fun onDrawerStateChanged(newState: Int) {
          }

          override fun onDrawerClosed(drawerView: View) {
           //关闭滑动菜单的时候关闭输入法
              val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
              manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
          }
      })
    }

    private fun showWeatherInfo(weather:Weather){
        weatherBind.nowBind .placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now布局中的数据
        val currentTempText = "${realtime.temperature.toInt()}℃"
        weatherBind.nowBind.currentTemp.text = currentTempText
        weatherBind.nowBind.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        weatherBind.nowBind.currentAQI.text = currentPM25Text
        weatherBind.nowBind.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast布局中的数据
        weatherBind.forecastBind.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,weatherBind.forecastBind.forecastLayout,false)
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
            weatherBind.forecastBind.forecastLayout.addView(view)
        }

        //填充life_index布局中的数据
        val lifeIndex = daily.lifeIndex
        weatherBind.lifeBind.coldRiskText.text = lifeIndex.coldRisk[0].desc
        weatherBind.lifeBind.dressingText.text = lifeIndex.dressing[0].desc
        weatherBind.lifeBind.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        weatherBind.lifeBind.carWashText.text = lifeIndex.carWashing[0].desc
        weatherBind.weatherLayout.visibility = View.VISIBLE
    }

    //刷新天气
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        weatherBind.swipeRefresh.isRefreshing = true
    }
}