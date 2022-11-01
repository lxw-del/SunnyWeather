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
本项目的沉浸式布局的处理方法
```kotlin
//让背景和状态栏融合到一起，沉浸式布局
        val window = this.window.apply {
            statusBarColor = Color.TRANSPARENT
        }
        setStatusBarTextColor(this,Color.TRANSPARENT)
        //false表示沉浸，true表示不沉浸
        WindowCompat.setDecorFitsSystemWindows(window,false)
        setStatusBarVisible(this,false)

//因为这里使用到的是FrameLayout并不会对我们的布局控件进行偏移处理，会导致控件和状态栏覆盖，这里的处理方法是这样
 val navBtn = findViewById<Button>(R.id.navBtn)
      //借助setOnApplyWindowInsetsListener函数去监听Windowsets发生变化的时间，当有监听到发生变化的时候，我们可       以读取顶部insets的大小，然后对控件进行偏移
      //这个方法是因为FrameLayout不会对我们的控件进行偏移，所以需要我们自己去偏移，否则会有些不兼容的效果
      ViewCompat.setOnApplyWindowInsetsListener(navBtn){view,insets->
          val params = view.layoutParams as FrameLayout.LayoutParams
          params.topMargin = getStatusBarHeight(this)
          insets
      }

```