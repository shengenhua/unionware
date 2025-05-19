package com.unionware.basicui.main.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.api.basic.BasicApi
import unionware.base.network.requestNotLifecycle
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class DeviceHeartService : Service() {
    class DeviceHeartService : Binder()

    @Inject
    @JvmField
    var api: BasicApi? = null

    // 创建一个大小为 5 的线程池
    var executor = Executors.newScheduledThreadPool(5);

    override fun onBind(intent: Intent): IBinder = DeviceHeartService()

    override fun onCreate() {
        super.onCreate()
        //延迟 0 分钟后开始执行任务，然后在上一次任务执行完成后，等待 2 分钟再执行下一次任务
        executor.scheduleWithFixedDelay({
            // 执行任务
            api?.deviceHeartbeat()?.requestNotLifecycle {

            }
        }, 0, 2, TimeUnit.MINUTES)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown();
    }
}