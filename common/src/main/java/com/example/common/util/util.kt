package com.example.common.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.example.common.BaseApp

fun getString(@StringRes id: Int): String {
    val context = BaseApp.instance
    return context.getString(id)
}

fun getColor(@ColorRes id: Int): Int {
    val context = BaseApp.instance
    return context.getColor(id)
}

fun getDrawable(@DrawableRes id: Int): Drawable {
    val context = BaseApp.instance
    return ContextCompat.getDrawable(context, id)!!
}


fun <T : Activity> Context.startActivity(tClass: Class<T>) {
    val intent = android.content.Intent(this, tClass)
    startActivity(intent)
}

fun View.layout(
    x: Int,
    y: Int,
) {
    layout(x, y, x + measuredWidth, y + measuredHeight)
}

val dpf = BaseApp.instance.resources.displayMetrics.density

val dpi = dpf.toInt()

val Int.dpi
    get() = this * com.example.common.util.dpi

val Int.dpf
    get() = this * com.example.common.util.dpf

fun ViewGroup.textView(
    w: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    h: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    attach: Boolean = true,
    block: (TextView.() -> Unit),
) = AppCompatTextView(context).apply {
    layoutParams = marginLayoutParams(w, h)
    block.invoke(this)
    if (attach) addView(this)
}

fun marginLayoutParams(
    width: Int,
    height: Int,
): ViewGroup.MarginLayoutParams {
    return ViewGroup.MarginLayoutParams(width, height)
}

fun View.setMargins(
    l: Int = 0,
    t: Int = 0,
    r: Int = 0,
    b: Int = 0,
) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(l, t, r, b)
}

fun TextView.setAttributes(textSize: Float, @ColorRes resId: Int, fontWeight: Int = 500) {
    this.textSize = textSize
    setFontWeight(fontWeight)
    this.setTextColor(getColor(resId))
}

fun TextView.setFontWeight(weight: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        typeface = Typeface.create(null, weight, false)
    } else {
        when (weight) {
            400 -> typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            600 -> typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            700 -> typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
        }
    }
}

val View?.heightUsed: Int
    get() {
        this ?: return 0
        if (parent != null && visibility != View.GONE) {
            return marginTop + marginBottom + measuredHeight
        }
        return 0
    }

const val wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT
const val matchParent = ViewGroup.LayoutParams.MATCH_PARENT

fun Context.checkPermission(permission : String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission,
    ) == PackageManager.PERMISSION_GRANTED
}

fun toast(msg: String) {
    Toast.makeText(BaseApp.instance, msg, Toast.LENGTH_SHORT).show()
}