package com.example.wingbu.usetimestatistic.utils

import android.util.Log

/**
 * Created by Wingbu on 2017/7/18.
 */
object StringUtils {
    //按行写入文件的字符串，格式如下：
    @JvmStatic
    fun getInputString(timeStamp: Long, className: String, type: Int, activeTime: Long): String {
        var input: String? = null
        input = if (activeTime > 0) {
            """$timeStamp  (${DateTransUtils.stampToDate(timeStamp)})  $className  $type  $activeTime
"""
        } else {
            """$timeStamp  (${DateTransUtils.stampToDate(timeStamp)})  $className  $type
"""
        }
        return input
    }

    //截取文件名，去除后缀
    @JvmStatic
    fun getFileNameWithoutSuffix(fileName: String): String {
        val fileNameSplit = fileName.split("\\.").toTypedArray()
        if (fileNameSplit.size > 1) {
            return fileNameSplit[0]
        }
        return "-1"
    }

    //将按行写入文件的字符串，解析出时间戳
    @JvmStatic
    fun getTimeStampFromString(record: String?): Long {
        if (record == null) {
            return 0
        }
        val temp = record.split("  ").toTypedArray()
        if (temp.isEmpty()) {
            return 0
        }
        return temp[0].toLong()
    }
}