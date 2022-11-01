# SunnyWeather

仍在更新中。。。

新增了通过Palette框架来提取背景图片的颜色，并且通过calculateLuminance来计算颜色深度，通过判断深度，设置浅色模式或者深色模式
```kotlin
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
```

同时，这次更新提供了获取屏幕宽度的方法，以及获取状态栏高度的方法，我们只需要去提取状态栏区域的颜色深度。
```kotlin
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
```