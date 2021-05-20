package com.example.wingbu.usetimestatistic.file

import android.util.Log
import com.example.wingbu.usetimestatistic.utils.DateTransUtils
import com.example.wingbu.usetimestatistic.utils.StringUtils.getTimeStampFromString
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

/**
 * Created by Wingbu on 2017/7/18.
 */
object ReadRecordFileUtils {
    const val TAG = "EventUtils"

    // =======================================
    // Read  Files
    // =======================================
    @JvmStatic
    fun getRecordStartTime(baseFilePath: String, timeStamp: Long): Long {
        return getTimeStampFromString(getFirstStringLines(baseFilePath, timeStamp))
    }

    @JvmStatic
    fun getRecordEndTime(baseFilePath: String, timeStamp: Long): Long {
        val list = getAllStringLines(baseFilePath, timeStamp)
        if (list == null || list.size < 1) {
            return 0
        }
        Log.i(
            TAG,
            "ReadRecordFileUtils--getRecordEndTime()   以行为单位读取文件内容，读最后一行：" + list[list.size - 1]
        )
        if (list[list.size - 1] == null) {
            Log.i(
                TAG,
                "ReadRecordFileUtils--getRecordEndTime()   以行为单位读取文件内容，读倒数第二行：" + list[list.size - 2]
            )
            return getTimeStampFromString(list[list.size - 2])
        }
        return getTimeStampFromString(list[list.size - 1])
    }

    fun getAllStringLines(baseFilePath: String, timeStamp: Long): ArrayList<String?>? {
        val fileName = DateTransUtils.getZeroClockTimestamp(timeStamp)
        val file = File("$baseFilePath/$fileName.txt")
        if (!file.exists()) {
            Log.i(TAG, "读取RecordFile文件内容时，文件不存在")
            return null
        }
        var reader: BufferedReader? = null
        var line: String? = null
        val stringsArray = ArrayList<String?>()
        try {
            reader = BufferedReader(FileReader(file))
            // 一次读入一行，直到读入null为文件结束
            while (reader.readLine().also { line = it } != null) {
                Log.i("reader", "      $line")
                stringsArray.add(line)
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e1: IOException) {
                }
            }
        }
        return stringsArray
    }

    fun getFirstStringLines(baseFilePath: String, timeStamp: Long): String? {
        val fileName = DateTransUtils.getZeroClockTimestamp(timeStamp)
        val file = File("$baseFilePath/$fileName.txt")
        if (!file.exists()) {
            Log.i(TAG, "读取RecordFile文件内容时，文件不存在")
            return null
        }
        var reader: BufferedReader? = null
        var tempString: String? = null
        try {
            reader = BufferedReader(FileReader(file))
            val line = 1
            // 一次读入一行，直到读入null为文件结束
            while (reader.readLine().also { tempString = it } != null) {
                // 显示行号
                if (line == 1) {
                    break
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e1: IOException) {
                }
            }
        }
        Log.i(TAG, "ReadRecordFileUtils--getRecordStartTime()  以行为单位读取文件内容，读第一行：$tempString")
        return tempString
    }

    val allFileNames: Array<String>
        get() {
            val file = File(WriteRecordFileUtils.BASE_FILE_PATH)
            return file.list()
        }
}