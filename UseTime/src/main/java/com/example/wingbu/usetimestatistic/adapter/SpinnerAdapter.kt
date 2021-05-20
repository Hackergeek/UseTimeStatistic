package com.example.wingbu.usetimestatistic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.wingbu.usetimestatistic.R
import java.util.*

/**
 * 主界面中日期选择的SpinnerAdapter
 *
 * （不好用，所以弃坑了）
 *
 * Created by Wingbu on 2017/8/28.
 */
class SpinnerAdapter : BaseAdapter {
    private var mList: ArrayList<String>
    private var mContext: Context? = null

    constructor(mList: ArrayList<String>, mContext: Context?) {
        this.mList = mList
        this.mContext = mContext
    }

    constructor(mList: ArrayList<String>) {
        this.mList = mList
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getItem(i: Int): Any {
        return mList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        var view = view
        view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_date, null)
        if (view != null) {
            val TextView1 = view.findViewById<View>(R.id.tv_date) as TextView
            TextView1.text = mList[i]
        }
        return view
    }
}