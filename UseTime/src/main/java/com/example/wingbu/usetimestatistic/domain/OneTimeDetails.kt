package com.example.wingbu.usetimestatistic.domain

import android.annotation.TargetApi
import android.app.usage.UsageEvents
import android.os.Build
import com.example.wingbu.usetimestatistic.utils.DateTransUtils.stampToDate
import java.util.*

/**
 * 记录打开一次应用的时候，其中的详情，包括这次打开的应用名，使用时长，以及打开了哪些activity，及各个activity的使用详情
 *
 * Created by Wingbu on 2017/7/18.
 */
class OneTimeDetails(
    var pkgName: String?,
    var useTime: Long,
    var oneTimeDetailEventList: ArrayList<UsageEvents.Event>
) {

    //startTime = DateUtils.formatSameDayTime(OneTimeDetailEventList.get(0).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
    @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
    val startTime: String?
        get() {
            var startTime: String? = null
            if (oneTimeDetailEventList.size > 0) {
                //startTime = DateUtils.formatSameDayTime(OneTimeDetailEventList.get(0).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
                startTime = stampToDate(oneTimeDetailEventList[0]!!.timeStamp)
            }
            return startTime
        }

    //stopTime = DateUtils.formatSameDayTime(OneTimeDetailEventList.get(OneTimeDetailEventList.size()-1).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
    @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
    val stopTime: String?
        get() {
            var stopTime: String? = null
            if (oneTimeDetailEventList.size > 0) {
                //stopTime = DateUtils.formatSameDayTime(OneTimeDetailEventList.get(OneTimeDetailEventList.size()-1).getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
                stopTime = stampToDate(
                    oneTimeDetailEventList[oneTimeDetailEventList.size - 1]!!.timeStamp
                )
            }
            return stopTime
        }
}