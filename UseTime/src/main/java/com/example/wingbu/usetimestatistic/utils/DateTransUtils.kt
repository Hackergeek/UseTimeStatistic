package com.example.wingbu.usetimestatistic.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Wingbu on 2017/7/18.
 */
object DateTransUtils {
    private val dateFormat = SimpleDateFormat("M-d-yyyy")
    const val DAY_IN_MILLIS = (24 * 60 * 60 * 1000).toLong()

    /*
     * 将时间戳转换为时间
     */
    fun stampToDate(stamp: String): String {
        val res: String
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val lt: Long = stamp.toLong()
        val date = Date(lt)
        res = simpleDateFormat.format(date)
        return res
    }

    @JvmStatic
    fun stampToDate(stamp: Long): String {
        val res: String
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(stamp)
        res = simpleDateFormat.format(date)
        return res
    }

    //获取今日某时间的时间戳
    fun getTodayStartStamp(hour: Int, minute: Int, second: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal[Calendar.HOUR_OF_DAY] = hour
        cal[Calendar.MINUTE] = minute
        cal[Calendar.SECOND] = second
        val todayStamp = cal.timeInMillis
        Log.i(
            "Wingbu",
            " DateTransUtils-getTodayStartStamp()  获取当日" + hour + ":" + minute + ":" + second + "的时间戳 :" + todayStamp
        )
        return todayStamp
    }

    //获取当日00:00:00的时间戳,东八区则为早上八点
    @JvmStatic
    fun getZeroClockTimestamp(time: Long): Long {
        var currentStamp = time
        currentStamp -= currentStamp % DAY_IN_MILLIS
        Log.i(
            "Wingbu",
            " DateTransUtils-getZeroClockTimestamp()  获取当日00:00:00的时间戳,东八区则为早上八点 :$currentStamp"
        )
        return currentStamp
    }

    //获取最近7天的日期,用于查询这7天的系统数据
    @JvmStatic
    val searchDays: ArrayList<String>
        get() {
            val dayList = ArrayList<String>()
            for (i in 0..6) {
                dayList.add(getDateString(i))
            }
            return dayList
        }

    //获取dayNumber天前，当天的日期字符串
    private fun getDateString(dayNumber: Int): String {
        val time = System.currentTimeMillis() - dayNumber * DAY_IN_MILLIS
        Log.i("Wingbu", " DateTransUtils-getDateString()  获取查询的日期 :" + dateFormat.format(time))
        return dateFormat.format(time)
    }
}