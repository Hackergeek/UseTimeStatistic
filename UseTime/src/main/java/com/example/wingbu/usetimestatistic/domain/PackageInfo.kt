package com.example.wingbu.usetimestatistic.domain

/**
 * 统计数据---记录每个应用的包名，使用时长和使用次数
 *
 * Created by Wingbu on 2017/7/18.
 */
class PackageInfo(
    var usedCount: Int,
    var usedTime: Long,
    var packageName: String
) {
    fun addCount() {
        usedCount++
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PackageInfo

        if (usedCount != other.usedCount) return false
        if (usedTime != other.usedTime) return false
        if (packageName != other.packageName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = usedCount
        result = 31 * result + usedTime.hashCode()
        result = 31 * result + packageName.hashCode()
        return result
    }


}