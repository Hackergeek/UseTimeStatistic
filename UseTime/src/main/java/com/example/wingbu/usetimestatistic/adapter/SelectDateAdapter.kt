package com.example.wingbu.usetimestatistic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.wingbu.usetimestatistic.R
import com.example.wingbu.usetimestatistic.adapter.SelectDateAdapter.SelectDateViewHolder
import java.util.*

/**
 * 主界面日期选择
 *
 * Created by Wingbu on 2017/10/16.
 */
class SelectDateAdapter(private val mDateList: ArrayList<String>) :
    RecyclerView.Adapter<SelectDateViewHolder>() {
    private var mOnItemClickListener: OnRecyclerViewItemClickListener? = null

    //define interface
    interface OnRecyclerViewItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnRecyclerViewItemClickListener?) {
        mOnItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectDateViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.selece_date_item, parent, false)
        return SelectDateViewHolder(v)
    }

    override fun onBindViewHolder(holder: SelectDateViewHolder, position: Int) {
        holder.tvItemSelectDate.text = mDateList[position]
        holder.itemView.setOnClickListener { v ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(v, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDateList.size
    }

    inner class SelectDateViewHolder(itemView: View) : ViewHolder(itemView) {
        var tvItemSelectDate: TextView = itemView.findViewById<View>(R.id.tv_item_select_date) as TextView

    }
}