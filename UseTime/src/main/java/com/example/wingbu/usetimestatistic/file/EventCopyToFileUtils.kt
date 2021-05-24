package com.example.wingbu.usetimestatistic.file

import android.annotation.TargetApi
import android.app.usage.UsageEvents
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.wingbu.usetimestatistic.utils.DateTransUtils
import com.example.wingbu.usetimestatistic.utils.EventUtils.getEventList
import com.example.wingbu.usetimestatistic.utils.StringUtils.getFileNameWithoutSuffix
import com.example.wingbu.usetimestatistic.utils.StringUtils.getInputString
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * 将系统记录的events数据取出，按照一定的格式记录于文件中
 *
 * Created by Wingbu on 2017/7/18.
 */
object EventCopyToFileUtils {
    private const val TAG = "EventUtils"
    private const val BASE_FILE_PATH = "/data/data/com.example.wingbu.usetimestatistic/files/event_copy"
    private const val MAX_FILE_NUMBER = 7
    @JvmStatic
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun write(context: Context, startTime: Long, endTime: Long) {
        val eventList = getEventList(context, startTime, endTime)
        if (eventList.size == 0) {
            return
        }
        val fileName = DateTransUtils.getZeroClockTimestampDongbaDistrict(startTime)
        val filePath = "$BASE_FILE_PATH/$fileName.txt"
        try {
            checkFile(filePath)

            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            val writer = FileWriter(filePath, true)
            var lastEvent: UsageEvents.Event? = null
            for (i in eventList.indices) {
                if (context.packageName == eventList[i].packageName) {
                    Log.i(TAG, "   " + eventList[i].className)
                    val thisEvent = eventList[i]
                    if (lastEvent != null && lastEvent.eventType == 1 && thisEvent.eventType == 2 && lastEvent.className == thisEvent.className) {
                        writer.write(
                            getInputString(
                                thisEvent.timeStamp,
                                thisEvent.className,
                                thisEvent.eventType,
                                thisEvent.timeStamp - lastEvent.timeStamp
                            )
                        )
                    } else {
                        writer.write(
                            getInputString(
                                thisEvent.timeStamp,
                                thisEvent.className,
                                thisEvent.eventType,
                                0
                            )
                        )
                    }
                    lastEvent = thisEvent
                }
            }
            writer.close()
            Log.i(TAG, " WriteRecordFileUtils--writeToFile()  写入文件成功 $fileName")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkFile(filePath: String) {
        val baseFile = File(BASE_FILE_PATH)
        if (!baseFile.exists()) {
            baseFile.mkdirs()
        }
        val file = File(filePath)
        if (!file.exists()) {
            //如果文件不存在，则创建文件
            try {
                if (file.createNewFile()) {
                    Log.i(TAG, "  EventCopyToFileUtils--checkFile()    文件创建成功 : $filePath")
                } else {
                    Log.i(TAG, "  EventCopyToFileUtils--checkFile()   文件创建失败 : $filePath")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.i(TAG, "  EventCopyToFileUtils--checkFile()   文件创建失败！ : " + e.message)
            }
        } else {
            //如果文件已经存在，则清空文件,以便重新写入
            try {
                // 打开一个写文件器，构造函数中的第二个参数false表示以覆盖形式写文件
                val writer = FileWriter(filePath, false)
                writer.write("")
                writer.close()
                Log.i(TAG, "  EventCopyToFileUtils--checkFile()   文件已经存在，则清空文件 ")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        //每次新建文件，都会检查是否需要删除多余文件
        deleteRedundantFile()
    }

    private fun deleteRedundantFile() {
        val baseFile = File(BASE_FILE_PATH)
        val files = baseFile.list()
        if (files == null || files.isEmpty()) {
            Log.i(TAG, "  EventCopyToFileUtils--deleteRedundantFile()     没有可以删除的文件  ")
            return
        }
        if (files.size <= MAX_FILE_NUMBER) {
            return
        }
        val fileName = oldestFileName
        val file = File("$BASE_FILE_PATH/$fileName.txt")
        if (file.exists()) {
            try {
                if (file.delete()) {
                    Log.i(
                        TAG,
                        "  EventCopyToFileUtils--deleteRedundantFile()     文件删除成功 : $fileName"
                    )
                } else {
                    Log.i(
                        TAG,
                        "  EventCopyToFileUtils--deleteRedundantFile()     文件删除失败 : $fileName"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(
                    TAG,
                    "  EventCopyToFileUtils--deleteRedundantFile()     文件删除失败！ : " + e.message
                )
            }
        }
    }

    private val oldestFileName: Long
        private get() {
            try {
                var timeStamp = 999999999999999999L
                val file = File(BASE_FILE_PATH)
                val fileName = file.list()
                if (fileName == null || fileName.isEmpty()) {
                    Log.i(TAG, "  EventCopyToFileUtils--getOldestFileName()   没有之前最久写入的文件  ")
                    return 0
                }
                for (i in fileName.indices) {
                    if (timeStamp > getFileNameWithoutSuffix(
                            fileName[i]
                        ).toLong()
                    ) {
                        timeStamp = getFileNameWithoutSuffix(
                            fileName[i]
                        ).toLong()
                    }
                }
                Log.i(TAG, "  EventCopyToFileUtils--getOldestFileName()    : 之前最久写入的文件  $timeStamp")
                return timeStamp
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(
                    TAG,
                    "  EventCopyToFileUtils--getOldestFileName()   寻找之前最久写入的文件失败！ : " + e.message
                )
            }
            return 0
        }
}