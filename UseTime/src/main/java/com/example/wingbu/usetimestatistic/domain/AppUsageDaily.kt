package com.example.wingbu.usetimestatistic.domain

import java.util.*

/**
 * 统计数据---记录一天当中的统计数据
 * mStartTimeStamp :统计开始的时间
 * mEndTimeStamp : 统计结束的时间
 * mFlag : event获取的数据 和 usage获取的数据 是否匹配的标志位，
 * example: "equal" : 统计Usage数据 和 event数据，但是两者大致匹配
 * "unequal" : 统计Usage数据 和 event数据，但是两者大致匹配
 * "usage" : 仅查询并统计Usage数据，无event统计数据
 * mPackageInfoListByEvent : 根据event获取的统计数据
 * mPackageInfoListByUsage : 根据Usage获取的统计数据
 *
 * Created by Wingbu on 2017/7/18.
 */
data class AppUsageDaily(
    var startTimeStamp: Long,
    var endTimeStamp: Long,
    var flag: String,
    var packageInfoListByEvent: ArrayList<PackageInfo>,
    var packageInfoListByUsage: ArrayList<PackageInfo>
)