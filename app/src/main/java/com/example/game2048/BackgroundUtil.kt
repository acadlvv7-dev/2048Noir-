package com.asadbek.noir2048

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import kotlin.math.log2

object BackgroundUtil {
    private val map = hashMapOf(
        0 to R.drawable.bg_0,
        2 to R.drawable.bg_2,
        4 to R.drawable.bg_4,
        8 to R.drawable.bg_8,
        16 to R.drawable.bg_16,
        32 to R.drawable.bg_32,
        64 to R.drawable.bg_64,
        128 to R.drawable.bg_128,
        256 to R.drawable.bg_256,
        512 to R.drawable.bg_512,
        1024 to R.drawable.bg_1024,
        2048 to R.drawable.bg_2048,
        4096 to R.drawable.bg_4096,
        8192 to R.drawable.bg_8192,
    )

    private val cache = mutableMapOf<Int, Drawable>()

    fun getItemBackground(context: Context, x: Int): Drawable? {
        if (cache.containsKey(x)) return cache[x]

        val drawable = if (map.containsKey(x)) {
            ContextCompat.getDrawable(context, map[x]!!)
        } else {
            val gd = GradientDrawable()
            gd.cornerRadius = 16f * context.resources.displayMetrics.density
            val power = (log2(x.toDouble())).toInt()
            val hue = (power * 45 % 360).toFloat()
            val color = Color.HSVToColor(floatArrayOf(hue, 0.8f, 0.9f))
            gd.setColor(color)
            gd.setStroke(4, Color.WHITE)
            gd
        }
        
        if (drawable != null) cache[x] = drawable
        return drawable
    }
}