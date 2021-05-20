package com.example.wingbu.usetimestatistic.adapter

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.wingbu.usetimestatistic.R
import com.example.wingbu.usetimestatistic.adapter.UseTimeAdapter.UseTimeViewHolder
import com.example.wingbu.usetimestatistic.domain.OneTimeDetails
import com.example.wingbu.usetimestatistic.domain.PackageInfo
import com.example.wingbu.usetimestatistic.domain.UseTimeDataManager
import java.util.*

/**
 * Created by Wingbu on 2017/7/19.
 */
class UseTimeAdapter(context: Context?, private var mPackageInfoList: ArrayList<PackageInfo>) :
    RecyclerView.Adapter<UseTimeViewHolder>() {
    private var packageManager: PackageManager? = null
    private val mUseTimeDataManager: UseTimeDataManager = UseTimeDataManager.getInstance(context)!!
    private var mOnItemClickListener: OnRecyclerViewItemClickListener? = null

    //define interface
    interface OnRecyclerViewItemClickListener {
        fun onItemClick(view: View?, pkg: String?)
    }

    fun setOnItemClickListener(listener: OnRecyclerViewItemClickListener?) {
        mOnItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UseTimeViewHolder {
        packageManager = parent.context.packageManager
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.used_time_item_layout, parent, false)
        return UseTimeViewHolder(v)
    }

    override fun onBindViewHolder(holder: UseTimeViewHolder, position: Int) {
        holder.tvIndex.text = (position + 1).toString()
        try {
            holder.ivIcon.setImageDrawable(packageManager!!.getApplicationIcon(mPackageInfoList[position].packageName))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        holder.tvUsedCount.text = "${mPackageInfoList[position].usedCount}"
        holder.tvCalculateUsedTime.text =
            "${mPackageInfoList[position].usedTime / 1000}s / " + DateUtils.formatElapsedTime(
                mPackageInfoList[position].usedTime / 1000
            )
        //DateTransUtils.formatElapsedTime(mPackageInfoList.get(position).usedTime/1000)
        holder.tvUsedTime.text =
            "${getTotalTimeInForegroundFromUsage(mPackageInfoList[position].packageName) / 1000} s"
        holder.itemView.setOnClickListener { v ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(v, mPackageInfoList[position].packageName)
            }
        }
    }

    override fun getItemCount(): Int {
        return mPackageInfoList.size
    }

    inner class UseTimeViewHolder(itemView: View) : ViewHolder(itemView) {
        var tvIndex: TextView = itemView.findViewById<View>(R.id.index) as TextView
        var ivIcon: ImageView = itemView.findViewById<View>(R.id.app_icon) as ImageView
        var tvUsedCount: TextView = itemView.findViewById<View>(R.id.use_count) as TextView
        var tvUsedTime: TextView = itemView.findViewById<View>(R.id.use_time) as TextView
        var tvCalculateUsedTime: TextView = itemView.findViewById<View>(R.id.calculate_use_time) as TextView

    }

    private fun calculateUseTime(list: ArrayList<OneTimeDetails>, pkg: String): Long {
        var useTime: Long = 0
        for (i in list.indices) {
            if (list[i].pkgName == pkg) {
                useTime += list[i].useTime
            }
        }
        return useTime
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getTotalTimeInForegroundFromUsage(pkg: String): Long {
        val stats = mUseTimeDataManager.getUsageStats(pkg) ?: return 0
        return stats.totalTimeInForeground
    }

    /**
     * totalTimeVisible和totalTimeInForeground这两个值相等
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getTotalTimeVisibleFromUsage(pkg: String): Long {
        val stats = mUseTimeDataManager.getUsageStats(pkg) ?: return 0
        return stats.totalTimeVisible
    }

}