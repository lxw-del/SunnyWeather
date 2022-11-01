package com.example.sunnyweather

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.palette.graphics.Palette

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

//提取图片颜色 Palette框架
//思路，你可以去使用这个函数去实现，状态栏文字跟随背景图片的主题颜色改变
fun getBackgroundColor(bitmap: Bitmap,activity: Activity,right:Int,bottom:Int) {
    Palette.from(bitmap)
        .setRegion(0, 0, right, bottom)
        .generate {
            it?.let { palette ->
                var mostPopularSwatch: Palette.Swatch? = null
                for (swatch in palette.swatches) {
                    if (mostPopularSwatch == null || swatch.population > mostPopularSwatch.population) {
                        mostPopularSwatch = swatch
                    }
                }
                mostPopularSwatch?.let { swatch ->
                    val luminance = ColorUtils.calculateLuminance(swatch.rgb)

                    WindowInsetsControllerCompat(
                        activity.window,
                        activity.window.decorView
                    ).isAppearanceLightStatusBars =
                        luminance >= 0.5
                }

            }
        }
}

//获取屏幕的宽度
fun getScreenWidth(activity: Activity):Int{
    //版本大于等于33就使用这个方法
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.windowManager.currentWindowMetrics.bounds.width()
    } else {
        //版本小于33使用这个方法
        val metrics = activity.resources.displayMetrics
        metrics.widthPixels
    }

}

//获取状态栏的高度
fun getStatusBarHeight(activity: Activity):Int{
    var result = 0
    //getIdentifier是使用字符串的形式获取资源Id
    val resourceId = activity.resources.getIdentifier("status_bar_height","dimen","android")
    if (resourceId > 0){
        //通过getDimensionPixelSize 返回dimens资源定义的像素数，返回的是像素数
        result = activity.resources.getDimensionPixelSize(resourceId)
    }
    return result
}