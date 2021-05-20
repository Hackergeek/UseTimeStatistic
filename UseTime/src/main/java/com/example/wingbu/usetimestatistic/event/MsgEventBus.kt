package com.example.wingbu.usetimestatistic.event

import org.greenrobot.eventbus.EventBus

/**
 * 消息传递工具（暂未使用）
 *
 * Created by Wingbu on 2017/7/18.
 */
object MsgEventBus {
    private var mEventBus: EventBus? = null
    @JvmStatic
    val instance: EventBus?
        get() {
            if (mEventBus == null) {
                mEventBus = EventBus()
            }
            return mEventBus
        }
}