package com.example.wingbu.usetimestatistic

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.example.wingbu.usetimestatistic.file.WriteRecordFileUtils
import com.example.wingbu.usetimestatistic.utils.StringUtils

/**
 * 应用的Application，用于在生命周期中记录相关的"Events"数据
 *
 * Created by Wingbu on 2017/9/13.
 */
class UseTimeApplication : Application() {
    //以下三个属性用于记录上次events的属性
    private var lastTime = 0L
    private var lastClassName: String? = null
    private var lastEventType = 0
    override fun onCreate() {
        super.onCreate()
        application = this

        //自行记录本应用的event, 在onPause()记录 event （type = 1）,在onResumed记录 event （type = 2）
        //用此event数据和系统记录的event数据对比，用于检测系统数据的有效性
        //对比方法：
        //    步骤一：点击actionbar的写入按钮，将系统数据写入一份到 "/data/data/com.example.wingbu.usetimestatistic/files/event_copy"目录下
        //    步骤二：自行记录本应用的event数据写入到 "/data/data/com.example.wingbu.usetimestatistic/files/event_log"目录下
        //    步骤三：将两个目录下的同名文件取出，使用文本对比软件Beyond Compare 进行对比
        // 一般而言，在不出现闪退和强杀的情况下，两文件记录的数据应当大致相同，仅仅只有时间戳的末几位不一样，一般误差在1秒之内，即大概只有末三位不一致
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {
                var activeTime = 0L
                val time = System.currentTimeMillis()
                val className = activity.componentName.className
                if (lastTime > 0 && lastClassName != null && lastEventType > 0 && lastEventType == 1 && lastClassName == className) {
                    activeTime = time - lastTime
                }
                Log.i(
                    "ActivityLifecycle",
                    StringUtils.getInputString(time, className, 2, activeTime)
                )
                WriteRecordFileUtils.write(time, className, 2, activeTime)
                resetData(time, className, 2)
            }

            override fun onActivityResumed(activity: Activity) {
                val time = System.currentTimeMillis()
                val className = activity.componentName.className
                Log.i("ActivityLifecycle", StringUtils.getInputString(time, className, 1, 0))
                WriteRecordFileUtils.write(time, className, 1, 0)
                resetData(time, className, 1)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
        })
    }

    private fun resetData(timeStamp: Long, className: String, type: Int) {
        lastTime = timeStamp
        lastClassName = className
        lastEventType = type
    }

    companion object {
        var application: UseTimeApplication? = null
    }
}