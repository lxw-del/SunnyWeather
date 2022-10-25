package com.example.sunnyweather

import android.app.Activity
import android.graphics.Color
import android.view.WindowInsetsController
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat

//用来定义修改状态栏属性的顶层方法文件

/**
 * 设置状态栏字体颜色
 */

fun setStatusBarTextColor(activity: Activity,@ColorInt color:Int) {
    //计算颜色深度
    val luminanceValue = ColorUtils.calculateLuminance(color)
    WindowInsetsControllerCompat(activity.window,activity.window.decorView).let {
        if (color == Color.TRANSPARENT){
            it.isAppearanceLightStatusBars = true
        }else{
            it.isAppearanceLightStatusBars = luminanceValue >= 0.5
        }
    }
}