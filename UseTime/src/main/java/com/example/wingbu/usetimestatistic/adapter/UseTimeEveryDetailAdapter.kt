package com.example.wingbu.usetimestatistic.adapter

import android.annotation.TargetApi
import android.app.usage.UsageEvents
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.wingbu.usetimestatistic.R
import com.example.wingbu.usetimestatistic.utils.DateTransUtils.stampToDate
import java.util.*

/**
 * Created by Wingbu on 2017/7/20.
 */
class UseTimeEveryDetailAdapter(private val mOneTimeDetailEventInfoList: ArrayList<UsageEvents.Event>) :
    RecyclerView.Adapter<UseTimeEveryDetailAdapter.UseTimeDetailViewHolder>() {
    private var packageManager: PackageManager? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UseTimeDetailViewHolder {
        packageManager = parent.context.packageManager
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_pkg_use_time_detail_item_layout, parent, false)
        return UseTimeDetailViewHolder(v)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: UseTimeDetailViewHolder, position: Int) {
        holder.tv_index.text = "" + (position + 1)
        try {
            holder.iv_icon.setImageDrawable(
                packageManager!!.getApplicationIcon(
                    mOneTimeDetailEventInfoList[position].packageName
                )
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        holder.tv_activity_name.text = mOneTimeDetailEventInfoList[position * 2].className
        holder.tv_activity_total_use_time.text =
            ((mOneTimeDetailEventInfoList[position * 2 + 1].timeStamp - mOneTimeDetailEventInfoList[position * 2].timeStamp) / 1000).toString() + "s / " + (mOneTimeDetailEventInfoList[position * 2 + 1].timeStamp - mOneTimeDetailEventInfoList[position * 2].timeStamp) + " ms"
        holder.tv_start_used_time.text =
            stampToDate(mOneTimeDetailEventInfoList[position * 2].timeStamp)
        holder.tv_stop_used_time.text =
            stampToDate(mOneTimeDetailEventInfoList[position * 2 + 1].timeStamp)
    }

    override fun getItemCount(): Int {
        return mOneTimeDetailEventInfoList.size / 2
    }

    inner class UseTimeDetailViewHolder(itemView: View) : ViewHolder(itemView) {
        var tv_index: TextView
        var iv_icon: ImageView
        var tv_activity_name: TextView
        var tv_activity_total_use_time: TextView
        var tv_start_used_time: TextView
        var tv_stop_used_time: TextView

        init {
            tv_index = itemView.findViewById<View>(R.id.index) as TextView
            iv_icon = itemView.findViewById<View>(R.id.app_icon) as ImageView
            tv_activity_name = itemView.findViewById<View>(R.id.activity_name) as TextView
            tv_activity_total_use_time =
                itemView.findViewById<View>(R.id.activity_total_use_time) as TextView
            tv_start_used_time = itemView.findViewById<View>(R.id.start_use_time) as TextView
            tv_stop_used_time = itemView.findViewById<View>(R.id.stop_use_time) as TextView
        }
    }
}