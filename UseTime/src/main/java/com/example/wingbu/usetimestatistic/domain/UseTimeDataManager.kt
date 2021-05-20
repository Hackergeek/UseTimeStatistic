package com.example.wingbu.usetimestatistic.domain

import android.annotation.TargetApi
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import com.example.wingbu.usetimestatistic.event.MessageEvent
import com.example.wingbu.usetimestatistic.event.MsgEventBus.instance
import com.example.wingbu.usetimestatistic.event.TimeEvent
import com.example.wingbu.usetimestatistic.utils.DateTransUtils
import com.example.wingbu.usetimestatistic.utils.DateTransUtils.getZeroClockTimestamp
import com.example.wingbu.usetimestatistic.utils.EventUtils.getEventList
import com.example.wingbu.usetimestatistic.utils.EventUtils.getUsageList
import java.lang.reflect.Field
import java.text.DateFormat
import java.util.*

/**
 * 主要的数据操作的类
 *
 * Created by Wingbu on 2017/7/18.
 */
class UseTimeDataManager(private val mContext: Context?) {
    var dayNum = 0
    //记录从系统中读取的数据
    private var mEventList: ArrayList<UsageEvents.Event>? = null
    private var mEventListChecked: ArrayList<UsageEvents.Event>? = null
    private var mStatsList: ArrayList<UsageStats>? = null

    //记录打开一次应用，使用的activity详情
    private val mOneTimeDetailList: ArrayList<OneTimeDetails> = ArrayList()

    //记录某一次打开应用的使用情况（查询某一次使用情况的时候，用于界面显示）
    var oneTimeDetails: OneTimeDetails? = null

    // =======================================
    // service use
    // =======================================
    //主界面数据
    private val pkgInfoListFromEventList = ArrayList<PackageInfo>()

    /**
     * 主要的数据获取函数
     *
     * @param dayNumber   查询若干天前的数据
     * @return int        0 : event usage 均查询到了
     * 1 : event 未查询到 usage 查询到了
     * 2 : event usage 均未查询到
     */
    fun refreshData(dayNumber: Int): Int {
        dayNum = dayNumber
        mEventList = getEventList(dayNumber)
        mStatsList = getUsageList(dayNumber)
        if (mEventList == null || mEventList!!.size == 0) {
            instance!!.post(MessageEvent("未查到events"))
            Log.i(TAG, " UseTimeDataManager-refreshData()   未查到events")
            if (mStatsList == null || mStatsList!!.size == 0) {
                instance!!.post(MessageEvent("未查到stats"))
                Log.i(TAG, " UseTimeDataManager-refreshData()   未查到stats")
                return 2
            }
            return 1
        }

        //获取数据之后，进行数据的处理
        mEventListChecked = eventListChecked
        refreshOneTimeDetailList(0)
        refreshPackageInfoList()
        sendEventBus()
        return 0
    }

    //分类完成，初始化主界面所用到的数据
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun refreshPackageInfoList() {
        pkgInfoListFromEventList.clear()
        for (i in mStatsList!!.indices) {
            val info = PackageInfo(
                0, calculateUseTime(
                    mStatsList!![i].packageName
                ), mStatsList!![i].packageName
            )
            pkgInfoListFromEventList.add(info)
        }
        for (n in pkgInfoListFromEventList.indices) {
            val pkg = pkgInfoListFromEventList[n].packageName
            for (m in mOneTimeDetailList.indices) {
                if (pkg == mOneTimeDetailList[m].pkgName) {
                    pkgInfoListFromEventList[n].addCount()
                }
            }
        }
    }

    //按照使用时间的长短进行排序，获取应用使用情况列表
    fun getPackageInfoListOrderByTime(): ArrayList<PackageInfo> {
        for (n in pkgInfoListFromEventList.indices) {
            for (m in n + 1 until pkgInfoListFromEventList.size) {
                if (pkgInfoListFromEventList[n].usedTime < pkgInfoListFromEventList[m].usedTime) {
                    val temp = pkgInfoListFromEventList[n]
                    pkgInfoListFromEventList[n] = pkgInfoListFromEventList[m]
                    pkgInfoListFromEventList[m] = temp
                }
            }
        }
        return pkgInfoListFromEventList
    }

    //按照使用次数的多少进行排序，获取应用使用情况列表
    fun getPackageInfoListOrderByCount(): ArrayList<PackageInfo> {
        for (n in pkgInfoListFromEventList.indices) {
            for (m in n + 1 until pkgInfoListFromEventList.size) {
                if (pkgInfoListFromEventList[n].usedCount < pkgInfoListFromEventList[m].usedCount) {
                    val temp = pkgInfoListFromEventList[n]
                    pkgInfoListFromEventList[n] = pkgInfoListFromEventList[m]
                    pkgInfoListFromEventList[m] = temp
                }
            }
        }
        return pkgInfoListFromEventList
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun sendEventBus() {
        val event = TimeEvent(0, 0)
        if (mEventListChecked != null && mEventListChecked!!.size > 0) {
            event.startTime = mEventListChecked!![0].timeStamp
            event.endTime = mEventListChecked!![mEventListChecked!!.size - 1].timeStamp
        }
        instance!!.post(event)
    }

    //从系统中获取event数据
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getEventList(dayNumber: Int): ArrayList<UsageEvents.Event> {
        val mEventList = ArrayList<UsageEvents.Event>()
        //        Calendar calendar = Calendar.getInstance();
//        long endTime = calendar.getTimeInMillis();
//        calendar.add(Calendar.YEAR, -1);
//        //long startTime = calendar.getTimeInMillis()- 3 * DateTransUtils.DAY_IN_MILLIS;
//        long startTime = calendar.getTimeInMillis();
        var endTime: Long = 0
        var startTime: Long = 0
        if (dayNumber == 0) {
            endTime = System.currentTimeMillis()
            startTime = getZeroClockTimestamp(endTime)
        } else {
            endTime =
                getZeroClockTimestamp(System.currentTimeMillis() - (dayNumber - 1) * DateTransUtils.DAY_IN_MILLIS) - 1
            startTime = endTime - DateTransUtils.DAY_IN_MILLIS + 1
        }
        return getEventList(mContext!!, startTime, endTime)
    }

    //从系统中获取UsageStat数据
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getUsageList(dayNumber: Int): ArrayList<UsageStats> {
        var endTime: Long = 0
        var startTime: Long = 0
        if (dayNumber == 0) {
            endTime = System.currentTimeMillis()
            startTime = getZeroClockTimestamp(endTime)
        } else {
            endTime =
                getZeroClockTimestamp(System.currentTimeMillis() - (dayNumber - 1) * DateTransUtils.DAY_IN_MILLIS) - 1
            startTime = endTime - DateTransUtils.DAY_IN_MILLIS + 1
        }
        return getUsageList(mContext!!, startTime, endTime)
    }

    //仅保留type为MOVE_TO_FOREGROUND和MOVE_TO_BACKGROUND的event
    @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private val eventListChecked: ArrayList<UsageEvents.Event>
        private get() {
            val mList = ArrayList<UsageEvents.Event>()
            for (i in mEventList!!.indices) {
                if (mEventList!![i].eventType == 1 || mEventList!![i].eventType == 2) {
                    mList.add(mEventList!![i])
                }
            }
            return mList
        }

    @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private val eventListCheckWithoutErrorData: ArrayList<UsageEvents.Event>
        private get() {
            val mList = ArrayList<UsageEvents.Event>()
            for (i in mEventList!!.indices) {
                if (mEventList!![i].eventType == 1 || mEventList!![i].eventType == 2) {
                    mList.add(mEventList!![i])
                }
            }
            return mList
        }

    //从 startIndex 开始分类event  直至将event分完
    //每次从0开始，将原本的 mOneTimeDetailList 清除一次,然后开始分类
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun refreshOneTimeDetailList(startIndex: Int) {
        if (startIndex == 0) {
            mOneTimeDetailList.clear()
        }
        var totalTime: Long = 0
        var usedIndex = 0
        var pkg: String? = null
        val list: ArrayList<UsageEvents.Event> = ArrayList<UsageEvents.Event>()
        for (i in startIndex until mEventListChecked!!.size) {
            if (i == startIndex) {
                if (mEventListChecked!![i].eventType == 2) {
                    Log.i(TAG, "refreshOneTimeDetailList()     warning : 每次打开一个app  第一个activity的类型是 2")
                }
                pkg = mEventListChecked!![i].packageName
                list.add(mEventListChecked!![i])
            } else {
                if (pkg != null) {
                    if (pkg == mEventListChecked!![i].packageName) {
                        list.add(mEventListChecked!![i])
                        if (i == mEventListChecked!!.size - 1) {
                            usedIndex = i
                        }
                    } else {
                        usedIndex = i
                        break
                    }
                }
            }
        }
        checkEventList(list)
        Log.i(TAG, "mEventListChecked 分类:  本次启动的包名：" + list[0]
                .packageName + "   时间：" + DateUtils.formatSameDayTime(
                list[0].timeStamp,
                System.currentTimeMillis(),
                DateFormat.MEDIUM,
                DateFormat.MEDIUM
            )
        )
        var i = 1
        while (i < list.size) {
            if (list[i].eventType == 2 && list[i - 1].eventType == 1) {
                totalTime += list[i].timeStamp - list[i - 1].timeStamp
            }
            i += 2
        }
        val oneTimeDetails = OneTimeDetails(pkg, totalTime, list)
        mOneTimeDetailList.add(oneTimeDetails)
        if (usedIndex < mEventListChecked!!.size - 1) {
            refreshOneTimeDetailList(usedIndex)
        }
    }

    fun getPkgOneTimeDetailList(pkg: String): ArrayList<OneTimeDetails> {
        if ("all" == pkg) {
            return mOneTimeDetailList
        }
        val list = ArrayList<OneTimeDetails>()
        if (mOneTimeDetailList.size > 0) {
            for (i in mOneTimeDetailList.indices) {
                if (mOneTimeDetailList[i].pkgName == pkg) {
                    list.add(mOneTimeDetailList[i])
                }
            }
        }
        return list
    }

    // 采用回溯的思想：
    // 从头遍历EventList，如果发现异常数据，则删除该异常数据，并从头开始再次进行遍历，直至无异常数据
    // （异常数据是指：event 均为 type=1 和type=2 ，成对出现，一旦发现未成对出现的数据，即视为异常数据）
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun checkEventList(list: ArrayList<UsageEvents.Event>) {
        var isCheckAgain = false
        var i = 0
        while (i < list.size - 1) {
            if (list[i]!!.className == list[i + 1]!!.className) {
                if (list[i]!!.eventType != 1) {
                    Log.i(
                        TAG, "   EventList 出错  ： " + list[i]!!
                            .packageName + "  " + DateUtils.formatSameDayTime(
                            list[i]!!.timeStamp,
                            System.currentTimeMillis(),
                            DateFormat.MEDIUM,
                            DateFormat.MEDIUM
                        ).toString()
                    )
                    list.removeAt(i)
                    isCheckAgain = true
                    break
                }
                if (list[i + 1]!!.eventType != 2) {
                    Log.i(
                        TAG, "   EventList 出错 ： " + list[i + 1]!!
                            .packageName + "  " + DateUtils.formatSameDayTime(
                            list[i + 1]!!.timeStamp,
                            System.currentTimeMillis(),
                            DateFormat.MEDIUM,
                            DateFormat.MEDIUM
                        ).toString()
                    )
                    list.removeAt(i)
                    isCheckAgain = true
                    break
                }
            } else {
                //i和i+1的className对不上，则删除第i个数据，重新检查
                list.removeAt(i)
                isCheckAgain = true
                break
            }
            i += 2
        }
        if (isCheckAgain) {
            checkEventList(list)
        }
    }

    @get:Throws(IllegalAccessException::class)
    @get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
    val pkgInfoListFromUsageList: ArrayList<PackageInfo>
        get() {
            val result = ArrayList<PackageInfo>()
            if (mStatsList != null && mStatsList!!.size > 0) {
                for (i in mStatsList!!.indices) {
                    result.add(
                        PackageInfo(
                            getLaunchCount(
                                mStatsList!![i]
                            ), mStatsList!![i].totalTimeInForeground, mStatsList!![i].packageName
                        )
                    )
                }
            }
            return result
        }

    // 利用反射，获取UsageStats中统计的应用使用次数
    @Throws(IllegalAccessException::class)
    private fun getLaunchCount(usageStats: UsageStats): Int {
        var field: Field? = null
        try {
            field = usageStats.javaClass.getDeclaredField("mLaunchCount")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return field!![usageStats] as Int
    }

    //根据event计算使用时间
    private fun calculateUseTime(pkg: String): Long {
        var useTime: Long = 0
        for (i in mOneTimeDetailList.indices) {
            if (mOneTimeDetailList[i].pkgName == pkg) {
                useTime += mOneTimeDetailList[i].useTime
            }
        }
        Log.i(TAG, "  calculateUseTime  :$pkg $useTime")
        return useTime
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getUsageStats(pkg: String): UsageStats? {
        for (i in mStatsList!!.indices) {
            if (mStatsList!![i].packageName == pkg) {
                return mStatsList!![i]
            }
        }
        return null
    }

    companion object {
        const val TAG = "Wingbu"
        private var mUseTimeDataManager: UseTimeDataManager? = null
        fun getInstance(context: Context?): UseTimeDataManager? {
            if (mUseTimeDataManager == null) {
                mUseTimeDataManager = UseTimeDataManager(context)
            }
            return mUseTimeDataManager
        }
    }
}