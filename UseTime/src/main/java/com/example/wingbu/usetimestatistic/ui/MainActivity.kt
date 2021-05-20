package com.example.wingbu.usetimestatistic.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wingbu.usetimestatistic.R
import com.example.wingbu.usetimestatistic.adapter.SelectDateAdapter
import com.example.wingbu.usetimestatistic.adapter.UseTimeAdapter
import com.example.wingbu.usetimestatistic.domain.UseTimeDataManager
import com.example.wingbu.usetimestatistic.event.MessageEvent
import com.example.wingbu.usetimestatistic.utils.DateTransUtils.searchDays
import org.greenrobot.eventbus.Subscribe
import java.util.*

/***
 * 主界面 -- 可以选择日期，然后查询所选日期的统计后的应用使用情况
 * Created by Wingbu on 2017/8/11.
 */
class MainActivity : BaseActivity() {
    private var mLlSelectDate: LinearLayout? = null
    private var mBtnDate: Button? = null
    private var mPopupWindow: PopupWindow? = null
    private lateinit var mRvSelectDate: RecyclerView
    private var mRecyclerView: RecyclerView? = null
    private var mUseTimeAdapter: UseTimeAdapter? = null
    private var mDateList: ArrayList<String>? = null
    private var mUseTimeDataManager: UseTimeDataManager? = null
    private val dayNum = 0
    private var isShowing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData(dayNum)
        initView()
    }

    @Subscribe
    fun showMessageEvent(event: MessageEvent) {
        Toast.makeText(baseContext, event.message, Toast.LENGTH_SHORT).show()
    }

    private fun initView() {
        mLlSelectDate = findViewById<View>(R.id.ll_select_date) as LinearLayout
        mBtnDate = findViewById<View>(R.id.tv_date) as Button
        mRecyclerView = findViewById<View>(R.id.rv_show_statistics) as RecyclerView
        showView(dayNum)
    }

    private fun initData(dayNum: Int) {
        mDateList = searchDays
        mUseTimeDataManager = UseTimeDataManager.getInstance(applicationContext)
        mUseTimeDataManager!!.refreshData(dayNum)
    }

    private fun showView(dayNumber: Int) {
        mBtnDate!!.text = mDateList!![dayNumber]
        mBtnDate!!.setOnClickListener {
            if (!isShowing) {
                showPopWindow()
            }
        }
        mUseTimeAdapter =
            UseTimeAdapter(this, mUseTimeDataManager!!.getPackageInfoListOrderByTime())
        mUseTimeAdapter!!.setOnItemClickListener(object :
            UseTimeAdapter.OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, pkg: String?) {
                showDetail(pkg)
            }
        })
        mRecyclerView!!.adapter = mUseTimeAdapter
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
    }

    private fun showDetail(pkg: String?) {
        val intent = Intent(this, UseTimeDetailActivity::class.java)
        intent.putExtra("type", "times")
        intent.putExtra("pkg", pkg)
        startActivity(intent)
    }

    /**
     * 显示日期选择框
     */
    private fun showPopWindow() {
        val contentView = LayoutInflater.from(this@MainActivity).inflate(R.layout.popuplayout, null)
        mPopupWindow = PopupWindow(contentView)
        mPopupWindow!!.width = ViewGroup.LayoutParams.MATCH_PARENT
        mPopupWindow!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mRvSelectDate = contentView.findViewById(R.id.rv_select_date)
        val adapter = SelectDateAdapter(mDateList!!)
        adapter.setOnItemClickListener(object : SelectDateAdapter.OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                mUseTimeDataManager!!.refreshData(position)
                showView(position)
                mPopupWindow!!.dismiss()
                isShowing = false
            }
        })
        mRvSelectDate.adapter = adapter
        mRvSelectDate.layoutManager = LinearLayoutManager(this)
        mPopupWindow!!.showAsDropDown(mBtnDate)
        isShowing = true
    }
}