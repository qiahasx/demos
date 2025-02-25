package com.example.view.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.util.dpf
import com.example.common.util.dpi
import com.example.common.util.generateRandomString
import com.example.common.util.getColor
import com.example.common.util.heightUsed
import com.example.common.util.layout
import com.example.common.util.marginLayoutParams
import com.example.common.util.matchParent
import com.example.common.util.setMargins
import com.example.common.util.textView
import com.example.common.util.toast
import com.example.common.util.wrapContent
import com.example.view.NestScrollViewModel
import com.example.view.R

class NestScrollViewImpl(context: Context) : MyNestedScrollingView(context) {
    private val viewModel by (context as ComponentActivity).viewModels<NestScrollViewModel>()
    private val colorView = View(context).apply {
        layoutParams = marginLayoutParams(matchParent, 300.dpi)
        setBackgroundColor(getColor(R.color.color_8f4c38))
        addView(this)
    }
    private val btnFirst = textView(wrapContent, 44.dpi) {
        setMargins(16.dpi, 8.dpi, 16.dpi, 8.dpi)
        setPadding(16.dpi, 0, 16.dpi, 0)
        gravity = Gravity.CENTER
        text = generateRandomString(1, 8)
        background = GradientDrawable().apply {
            setColor(getColor(R.color.color_e8d6d2))
            cornerRadius = 22.dpf
        }
        setOnClickListener {
            viewModel.selectTabIndex.postValue(1)
        }
    }
    private val btnSecond = textView(wrapContent, 44.dpi) {
        setMargins(16.dpi, 8.dpi, 16.dpi, 8.dpi)
        setPadding(16.dpi, 0, 16.dpi, 0)
        gravity = Gravity.CENTER
        text = generateRandomString(10, 10)
        background = GradientDrawable().apply {
            setColor(getColor(R.color.color_ffdbd1))
            cornerRadius = 22.dpf
        }
        setOnClickListener {
            viewModel.selectTabIndex.postValue(2)
        }
    }
    private val rvList = RecyclerView(context).apply {
        layoutParams = marginLayoutParams(matchParent, matchParent)
        layoutManager = LinearLayoutManager(context)
        adapter = Adapter()
        this@NestScrollViewImpl.addView(this)
    }

    init {
        layoutParams = marginLayoutParams(matchParent, matchParent)
    }

    override fun onConsumeScroll(y: Int) {
        val ratio = y.toFloat() / colorView.height
        colorView.alpha = (1 - ratio)
    }

    override fun getNestedScrollingChildView() = rvList

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildWithMargins(colorView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChildWithMargins(btnFirst, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChildWithMargins(btnSecond, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChildWithMargins(rvList, widthMeasureSpec, 0, heightMeasureSpec, btnFirst.heightUsed)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        colorView.run { layout(marginLeft, marginTop) }
        btnFirst.run { layout(marginLeft, colorView.height + marginTop) }
        btnSecond.run { layout(btnFirst.right + marginLeft, btnFirst.top) }
        rvList.run { layout(marginLeft, btnFirst.bottom + btnFirst.marginBottom + marginTop) }
    }

    private class Adapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val textView = TextView(parent.context).apply {
                layoutParams = marginLayoutParams(matchParent, 44.dpi)
                setMargins(16.dpi, 8.dpi, 16.dpi, 8.dpi)
                gravity = Gravity.CENTER
                setPadding(16.dpi, 0, 16.dpi, 0)
                background = GradientDrawable().apply {
                    setColor(getColor(R.color.color_ffdbd1))
                    cornerRadius = 3.dpf
                    setStroke(1.dpi, getColor(R.color.color_8f4c38))
                }
                setOnClickListener {
                    toast(this.text.toString())
                }
            }
            return object : RecyclerView.ViewHolder(textView) {}
        }

        override fun getItemCount() = 30

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as TextView).text = "$position:" + generateRandomString(10, 20)
        }
    }
}