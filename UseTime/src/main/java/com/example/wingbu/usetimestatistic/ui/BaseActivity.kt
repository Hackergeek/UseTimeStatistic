package com.example.wingbu.usetimestatistic.ui

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.wingbu.usetimestatistic.R
import com.example.wingbu.usetimestatistic.domain.UseTimeDataManager
import com.example.wingbu.usetimestatistic.file.EventCopyToFileUtils.write
import com.example.wingbu.usetimestatistic.file.ReadRecordFileUtils.getRecordEndTime
import com.example.wingbu.usetimestatistic.file.ReadRecordFileUtils.getRecordStartTime
import com.example.wingbu.usetimestatistic.file.WriteRecordFileUtils
import com.example.wingbu.usetimestatistic.utils.DateTransUtils

/**
 * Created by Wingbu on 2017/9/13.
 */
open class BaseActivity : Activity() {
    protected var mActionBar: ActionBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActionBar()
    }

    private fun initActionBar() {
        mActionBar = actionBar
        if (mActionBar != null) {
            mActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM //Enable自定义的View
            mActionBar!!.setCustomView(R.layout.actionbar_custom) //设置自定义的布局：actionbar_custom
            val tvActionTitle =
                mActionBar!!.customView.findViewById<TextView>(R.id.action_bar_title)
            tvActionTitle.setText(R.string.action_bar_title_1)
            val ivSetting = mActionBar!!.customView.findViewById<ImageView>(R.id.iv_setting)
            ivSetting.setOnClickListener { jumpToSystemPermissionActivity() }
            val ivWrite = mActionBar!!.customView.findViewById<ImageView>(R.id.iv_write)
            ivWrite.setOnClickListener {
                copyEventsToFile(
                    UseTimeDataManager.getInstance(
                        applicationContext
                    )!!.dayNum
                )
            }
            val ivContent = mActionBar!!.customView.findViewById<ImageView>(R.id.iv_content)
            ivContent.setOnClickListener { showDetail("all") }
        }
    }

    private fun jumpToSystemPermissionActivity() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    private fun showDetail(pkg: String) {
        val i = Intent()
        i.setClassName(this, "com.example.wingbu.usetimestatistic.ui.UseTimeDetailActivity")
        i.putExtra("type", "times")
        i.putExtra("pkg", pkg)
        startActivity(i)
    }

    private fun copyEventsToFile(dayNumber: Int) {
        var endTime: Long = 0
        var startTime: Long = 0
        val time = System.currentTimeMillis() - dayNumber * DateTransUtils.DAY_IN_MILLIS
        startTime = getRecordStartTime(WriteRecordFileUtils.BASE_FILE_PATH, time)
        endTime = getRecordEndTime(WriteRecordFileUtils.BASE_FILE_PATH, time)
        Toast.makeText(this, "已将系统数据写入本地文件", Toast.LENGTH_SHORT).show()
        Log.i(
            "BaseActivity",
            " BaseActivity--copyEventsToFile()    startTime = $startTime  endTime = $endTime"
        )
        write(this, startTime - 1000, endTime)
    }

    protected fun setActionBarTitle(title: String?) {
        if (mActionBar != null) {
            mActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM //Enable自定义的View
            mActionBar!!.setCustomView(R.layout.actionbar_custom) //设置自定义的布局：actionbar_custom
            val tvActionTitle =
                mActionBar!!.customView.findViewById<TextView>(R.id.action_bar_title)
            tvActionTitle.text = title
        }
    }

    protected fun setActionBarTitle(stringId: Int) {
        setActionBarTitle(getString(stringId))
    }
}