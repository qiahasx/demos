package com.example.demolist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.R
import com.example.common.util.dpf
import com.example.common.util.dpi
import com.example.common.util.edgeToEdgeCompat
import com.example.common.util.marginLayoutParams
import com.example.common.util.matchParent
import com.example.common.util.setAttributes
import com.example.common.util.setMargins
import com.example.common.util.startActivity

class DemoSelectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edgeToEdgeCompat()
        val demos = arrayOf(
            DemoItem("Demo1") { startActivity(Demo1Activity::class.java) },
            DemoItem("Demo2") { startActivity(Demo2Activity::class.java) },
            DemoItem("Demo3") { startActivity(Demo3Activity::class.java) },
        )
        val recyclerView = RecyclerView(this).apply {
            background = ColorDrawable(Color.DKGRAY)
            layoutParams = marginLayoutParams(matchParent, matchParent)
            layoutManager = LinearLayoutManager(this@DemoSelectActivity)
            adapter = DemoAdapter(demos)
        }
        setContentView(recyclerView)
    }

    private class DemoAdapter(val items: Array<DemoItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemView = TextView(parent.context).apply {
                layoutParams = marginLayoutParams(matchParent, 55.dpi)
                setMargins(16.dpi, 24.dpi, 16.dpi, 0)
                gravity = Gravity.CENTER
                setAttributes(24f, R.color.black, 600)
                background = GradientDrawable().apply {
                    setColor(com.example.common.util.getColor(R.color.white))
                    cornerRadius = 6.dpf
                }
            }
            return object : RecyclerView.ViewHolder(itemView) {}
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val textView = holder.itemView as? TextView ?: return
            textView.text = items[position].title
            textView.setOnClickListener { items[position].onClick() }
        }
    }

    private data class DemoItem(val title: String, val onClick: () -> Unit)
}