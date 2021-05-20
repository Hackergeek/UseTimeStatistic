package com.example.wingbu.usetimestatistic.utils

import android.annotation.TargetApi
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * 获取系统的数据，包括event和Usage
 *
 * Created by Wingbu on 2017/7/18.
 */
object EventUtils {
    private const val TAG = "EventUtils"
    private val dateFormat = SimpleDateFormat("M-d-yyyy HH:mm:ss", Locale.getDefault())
    @JvmStatic
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getEventList(
        context: Context,
        startTime: Long,
        endTime: Long
    ): ArrayList<UsageEvents.Event> {
        val mEventList = ArrayList<UsageEvents.Event>()
        val mUsmManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = mUsmManager.queryEvents(startTime, endTime)
        while (events.hasNextEvent()) {
            val e = UsageEvents.Event()
            events.getNextEvent(e)
            if (e.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND || e.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                mEventList.add(e)
            }
        }
        return mEventList
    }

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getUsageList(context: Context, startTime: Long, endTime: Long): ArrayList<UsageStats> {
        val list = ArrayList<UsageStats>()
        val mUsmManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val map = mUsmManager.queryAndAggregateUsageStats(startTime, endTime)
        for ((_, stats) in map) {
            if (stats.totalTimeInForeground > 0) {
                list.add(stats)
                Log.i(
                    TAG,
                    " EventUtils-getUsageList()   stats:" + stats.packageName + "   TotalTimeInForeground = " + stats.totalTimeInForeground
                )
            }
        }
        return list
    }
}