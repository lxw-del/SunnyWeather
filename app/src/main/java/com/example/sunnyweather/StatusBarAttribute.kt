package com.example.sunnyweather

import android.app.Activity
import android.graphics.Color
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import java.lang.reflect.Array.get

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
//状态栏显示控制
fun setStatusBarVisible(activity: Activity,isVisible:Boolean){
    val window = activity.window
    //设置内容是否铺满屏幕
    WindowCompat.setDecorFitsSystemWindows(window,isVisible)
    WindowInsetsControllerCompat(window,window.decorView).let {
        if (isVisible){
            it.show(WindowInsetsCompat.Type.statusBars())
        }else{
            it.hide(WindowInsetsCompat.Type.statusBars())
        }
        //控制状态栏的操作效果  SWIPE全屏时，你下拉状态栏，状态栏会在几秒后消失
        it.systemBarsBehavior = WindowInsetsControllerCompat
            .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}