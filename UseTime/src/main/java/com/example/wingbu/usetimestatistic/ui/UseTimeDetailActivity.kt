package com.example.wingbu.usetimestatistic.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wingbu.usetimestatistic.R
import com.example.wingbu.usetimestatistic.adapter.UseTimeDetailAdapter
import com.example.wingbu.usetimestatistic.adapter.UseTimeEveryDetailAdapter
import com.example.wingbu.usetimestatistic.domain.OneTimeDetails
import com.example.wingbu.usetimestatistic.domain.UseTimeDataManager

/**
 * 应用使用次数详情统计 （内容：当天，每次打开一个应用的使用信息）
 * 和 应用使用activity详情统计（内容：使用app一次，打开了哪些activity）
 * 以上两者公用此activity
 *
 * Created by Wingbu on 2017/9/11.
 */
class UseTimeDetailActivity : BaseActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mUseTimeDetailAdapter: UseTimeDetailAdapter? = null
    private var mUseTimeEveryDetailAdapter: UseTimeEveryDetailAdapter? = null
    private var mUseTimeDataManager: UseTimeDataManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_time_detail)
        Log.i(UseTimeDataManager.TAG, "  UseTimeDetailActivity      ")
        mUseTimeDataManager = UseTimeDataManager.getInstance(this)
        mRecyclerView = findViewById<View>(R.id.rv_use_time_detail) as RecyclerView
        val intent = intent
        val type = intent.getStringExtra("type")
        initView(type, intent)
    }

    private fun initView(type: String?, intent: Intent) {
        if ("times" == type) {
            //显示为次数统计信息
            showAppOpenTimes(intent.getStringExtra("pkg"))
            setActionBarTitle(R.string.action_bar_title_2)
        } else if ("details" == type) {
            //显示为activity统计信息
            showAppOpenDetails(intent.getStringExtra("pkg"))
            setActionBarTitle(R.string.action_bar_title_3)
        } else {
            Log.i(UseTimeDataManager.TAG, "   未知类型    ")
        }
    }

    private fun showAppOpenTimes(pkg: String?) {
        mUseTimeDetailAdapter = UseTimeDetailAdapter(
            mUseTimeDataManager!!.getPkgOneTimeDetailList(
                pkg!!
            )!!
        )
        mUseTimeDetailAdapter!!.setOnItemClickListener(object :
            UseTimeDetailAdapter.OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, details: OneTimeDetails?) {
                mUseTimeDataManager!!.oneTimeDetails = details
                showDetail(details!!.pkgName)
            }
        })
        mRecyclerView!!.adapter = mUseTimeDetailAdapter
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
    }

    private fun showAppOpenDetails(pkg: String?) {
        if (pkg != mUseTimeDataManager!!.oneTimeDetails!!.pkgName) {
            Log.i(UseTimeDataManager.TAG, "  showAppOpenDetails()    包名不一致 ")
        }
        mUseTimeEveryDetailAdapter =
            UseTimeEveryDetailAdapter(mUseTimeDataManager!!.oneTimeDetails!!.oneTimeDetailEventList)
        mRecyclerView!!.adapter = mUseTimeEveryDetailAdapter
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
    }

    private fun showDetail(pkg: String?) {
        val i = Intent()
        i.setClassName(this, "com.example.wingbu.usetimestatistic.ui.UseTimeDetailActivity")
        i.putExtra("type", "details")
        i.putExtra("pkg", pkg)
        startActivity(i)
    }
}