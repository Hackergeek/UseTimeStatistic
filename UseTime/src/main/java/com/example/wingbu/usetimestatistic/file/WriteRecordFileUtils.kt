package com.example.wingbu.usetimestatistic.file

import android.util.Log
import com.example.wingbu.usetimestatistic.utils.DateTransUtils
import com.example.wingbu.usetimestatistic.utils.StringUtils.getFileNameWithoutSuffix
import com.example.wingbu.usetimestatistic.utils.StringUtils.getInputString
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * 在本应用的生命周期中调用WriteRecordFileUtils，将记录本应用的events，用于和系统记录的events进行对比
 *
 * Created by Wingbu on 2017/7/18.
 */
object WriteRecordFileUtils {
    const val TAG = "WriteRecordFileUtils"
    const val BASE_FILE_PATH = "/data/data/com.example.wingbu.usetimestatistic/files/event_log"
    const val MAX_FILE_NUMBER = 7
    private var currentFileName: Long = 0

    // =======================================
    // Write  Files
    // =======================================
    fun write(timeStamp: Long, className: String, type: Int, activeTime: Long) {
        if (currentFileName == 0L) {
            currentFileName = latestFileName
        }
        val time = DateTransUtils.getZeroClockTimestamp(timeStamp)
        if (time == currentFileName) {
            writeToFile(time, timeStamp, className, type, activeTime)
        } else if (time > currentFileName) {
            createFile(time)
            writeToFile(time, timeStamp, className, type, activeTime)
        } else {
            Log.i(TAG, " WriteRecordFileUtils--write()    写入文件  时间有bug ")
        }
    }

    private fun writeToFile(
        fileName: Long,
        timeStamp: Long,
        className: String,
        type: Int,
        activeTime: Long
    ) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            val filePath = BASE_FILE_PATH + "/" + fileName + ".txt"
            val writer = FileWriter(filePath, true)
            val input_str = getInputString(timeStamp, className, type, activeTime)
            writer.write(input_str)
            writer.close()
            Log.i(TAG, " WriteRecordFileUtils--writeToFile()  写入文件成功 ")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i(TAG, " WriteRecordFileUtils--writeToFile()  写入文件错误 " + e.message)
        }
    }

    private fun createFile(fileName: Long) {
        val baseFile = File(BASE_FILE_PATH)
        if (!baseFile.exists()) {
            baseFile.mkdirs()
        }
        val file = File(BASE_FILE_PATH + "/" + fileName + ".txt")
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Log.i(TAG, "  WriteRecordFileUtils--createFile()    文件创建成功 : $fileName")
                } else {
                    Log.i(TAG, "  WriteRecordFileUtils--createFile()   文件创建失败 : $fileName")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.i(TAG, "  WriteRecordFileUtils--createFile()   文件创建失败！ : " + e.message)
            }
        }

        //每次新建文件，都会检查是否需要删除多余文件
        deleteRedundantFile()
    }

    private fun deleteRedundantFile() {
        val baseFile = File(BASE_FILE_PATH)
        val files = baseFile.list()
        if (files == null || files.size == 0) {
            Log.i(TAG, "  WriteRecordFileUtils--deleteFilePath()     没有可以删除的文件  ")
            return
        }
        if (files.size <= MAX_FILE_NUMBER) {
            return
        }
        val fileName = oldestFileName
        val file = File(BASE_FILE_PATH + "/" + fileName + ".txt")
        if (file.exists()) {
            try {
                if (file.delete()) {
                    Log.i(TAG, "  WriteRecordFileUtils--deleteFilePath()     文件删除成功 : $fileName")
                } else {
                    Log.i(TAG, "  WriteRecordFileUtils--deleteFilePath()     文件删除失败 : $fileName")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG, "  WriteRecordFileUtils--deleteFilePath()     文件删除失败！ : " + e.message)
            }
        }
    }

    //以时间戳为文件名
    private val latestFileName: Long
        private get() {
            try {
                var timeStamp: Long = 0
                val file = File(BASE_FILE_PATH)
                val fileName = file.list()
                if (fileName == null || fileName.size == 0) {
                    return 0
                }
                for (i in fileName.indices) {
                    if (timeStamp < getFileNameWithoutSuffix(
                            fileName[i]
                        ).toLong()
                    ) {
                        timeStamp = getFileNameWithoutSuffix(
                            fileName[i]
                        ).toLong()
                    }
                }
                currentFileName = timeStamp
                Log.i(
                    TAG,
                    "  WriteRecordFileUtils--getLatestFileName() : 最新写入的文件  " + currentFileName
                )
                return timeStamp
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(
                    TAG,
                    "  WriteRecordFileUtils--getLatestFileName()    寻找最新写入的文件失败！ : " + e.message
                )
            }
            return 0
        }
    private val oldestFileName: Long
        private get() {
            try {
                var timeStamp = 999999999999999999L
                val file = File(BASE_FILE_PATH)
                val fileName = file.list()
                if (fileName == null || fileName.size == 0) {
                    Log.i(TAG, "  WriteRecordFileUtils--getOldestFileName()   没有之前最久写入的文件  ")
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
                Log.i(TAG, "  WriteRecordFileUtils--getOldestFileName()    : 之前最久写入的文件  $timeStamp")
                return timeStamp
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(
                    TAG,
                    "  WriteRecordFileUtils--getOldestFileName()   寻找之前最久写入的文件失败！ : " + e.message
                )
            }
            return 0
        }
}