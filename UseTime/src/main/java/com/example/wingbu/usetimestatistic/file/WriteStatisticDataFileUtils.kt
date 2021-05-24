package com.example.wingbu.usetimestatistic.file

import android.util.Log
import com.example.wingbu.usetimestatistic.domain.AppUsageDaily
import com.example.wingbu.usetimestatistic.utils.DateTransUtils
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 * Created by Wingbu on 2017/7/18.
 */
object WriteStatisticDataFileUtils {
    const val TAG = "WriteStatisticDataFileUtils"
    const val BASE_FILE_PATH = "/data/data/com.example.wingbu.usetimestatistic/files/statics"
    const val FILE_NAME = "current.txt"
    const val MAX_FILE_SIZE = (10 * 1024 * 1024).toLong()

    // =======================================
    // Write  Files
    // =======================================
    fun write(AppUsageList: ArrayList<AppUsageDaily>) {
        checkFile()
        writeToFile(AppUsageList)
    }

    private fun writeToFile(appUsageList: ArrayList<AppUsageDaily>) {
        val file = File("$BASE_FILE_PATH/$FILE_NAME")
        if (!file.exists()) {
            createFile(file)
        }
        Log.i(TAG, " WriteStatisticDataFileUtils--writeToFile()  写入文件天数 :" + appUsageList.size)
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            val writer = FileWriter(file, true)
            val currentTime = System.currentTimeMillis()
            writer.write(
                """本次写入文件的时间为 : $currentTime   ${DateTransUtils.stampToDate(currentTime)}
"""
            )
            for (i in appUsageList.indices) {
                writeAppUsage(writer, appUsageList[i])
            }
            writer.close()
            Log.i(TAG, " WriteStatisticDataFileUtils--writeToFile()  写入文件成功 ")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun writeAppUsage(writer: FileWriter, appUsageDaily: AppUsageDaily) {
        try {
            writer.write(
                """当前数据所属日期 : ${appUsageDaily.startTimeStamp}   ${
                    DateTransUtils.stampToDate(
                        appUsageDaily.startTimeStamp
                    )
                }
"""
            )
            writer.write(
                """
    当前数据Flag : ${appUsageDaily.flag}
    
    """.trimIndent()
            )
            if (appUsageDaily.packageInfoListByEvent != null && appUsageDaily.packageInfoListByEvent.size > 0) {
                for (i in appUsageDaily.packageInfoListByEvent.indices) {
                    writer.write(
                        """event :  ${appUsageDaily.packageInfoListByEvent[i].packageName}  使用次数:${appUsageDaily.packageInfoListByEvent[i].usedCount}    使用时长:${appUsageDaily.packageInfoListByEvent[i].usedTime}
"""
                    )
                }
            }
            if (appUsageDaily.packageInfoListByUsage != null && appUsageDaily.packageInfoListByUsage.size > 0) {
                for (j in appUsageDaily.packageInfoListByUsage.indices) {
                    writer.write(
                        """usage :  ${appUsageDaily.packageInfoListByUsage[j].packageName}  使用次数:${appUsageDaily.packageInfoListByUsage[j].usedCount}    使用时长:${appUsageDaily.packageInfoListByUsage[j].usedTime}
"""
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkFile() {
        val file = File("$BASE_FILE_PATH/$FILE_NAME")
        if (file.exists()) {
            if (file.length() > MAX_FILE_SIZE) {
                val newFile =
                    File(BASE_FILE_PATH + "/" + System.currentTimeMillis() + "-rename.txt")
                file.renameTo(newFile)
            }
        } else {
            createFile(file)
        }
    }

    private fun createFile(file: File) {
        try {
            if (file.createNewFile()) {
                Log.i(TAG, "  WriteStatisticDataFileUtils--checkFile()    文件创建成功 : ")
            } else {
                Log.i(TAG, "  WriteStatisticDataFileUtils--checkFile()   文件创建失败 : ")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i(TAG, "  WriteStatisticDataFileUtils--checkFile()   文件创建失败！ : " + e.message)
        }
    }
}