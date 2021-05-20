package com.example.wingbu.usetimestatistic.adapter

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.wingbu.usetimestatistic.R
import com.example.wingbu.usetimestatistic.domain.OneTimeDetails
import java.util.*

/**
 * Created by Wingbu on 2017/7/20.
 */
class UseTimeDetailAdapter(private val mOneTimeDetailInfoList: ArrayList<OneTimeDetails>) :
    RecyclerView.Adapter<UseTimeDetailAdapter.UseTimeDetailViewHolder>() {
    private var packageManager: PackageManager? = null
    private var mOnItemClickListener: OnRecyclerViewItemClickListener? = null

    //define interface
    interface OnRecyclerViewItemClickListener {
        fun onItemClick(view: View?, details: OneTimeDetails?)
    }

    fun setOnItemClickListener(listener: OnRecyclerViewItemClickListener?) {
        mOnItemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UseTimeDetailViewHolder {
        packageManager = parent.context.packageManager
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.use_time_detail_item_layout, parent, false)
        return UseTimeDetailViewHolder(v)
    }

    override fun onBindViewHolder(holder: UseTimeDetailViewHolder, position: Int) {
        holder.tv_index.text = "" + (position + 1)
        try {
            holder.iv_icon.setImageDrawable(
                packageManager!!.getApplicationIcon(
                    mOneTimeDetailInfoList[position].pkgName!!
                )
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        holder.tv_start_used_time.text = mOneTimeDetailInfoList[position].startTime
        holder.tv_stop_used_time.text = mOneTimeDetailInfoList[position].stopTime
        holder.tv_total_used_time.text =
            (mOneTimeDetailInfoList[position].useTime / 1000).toString() + "s / " + mOneTimeDetailInfoList[position].useTime + " ms"
        holder.itemView.setOnClickListener { v ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(v, mOneTimeDetailInfoList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mOneTimeDetailInfoList.size
    }

    inner class UseTimeDetailViewHolder(itemView: View) : ViewHolder(itemView) {
        var tv_index: TextView
        var iv_icon: ImageView
        var tv_start_used_time: TextView
        var tv_stop_used_time: TextView
        var tv_total_used_time: TextView

        init {
            tv_index = itemView.findViewById<View>(R.id.index) as TextView
            iv_icon = itemView.findViewById<View>(R.id.app_icon) as ImageView
            tv_start_used_time = itemView.findViewById<View>(R.id.start_use_time) as TextView
            tv_stop_used_time = itemView.findViewById<View>(R.id.stop_use_time) as TextView
            tv_total_used_time = itemView.findViewById<View>(R.id.total_use_time) as TextView
        }
    }
}