package com.example.phattn.mapdirection

import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Global executor pools for the whole application.
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
@Singleton
class AppExecutor {

    private val mDiskIO: Executor
    private val mNetworkIO: Executor
    private val mMainThread: Executor

    @Inject
    constructor() : this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), MainThreadExecutor())

    constructor(diskIO: Executor, networkIO: Executor, mainThread: Executor) {
        mDiskIO = diskIO
        mNetworkIO = networkIO
        mMainThread = mainThread
    }

    fun diskIO() = mDiskIO
    fun networkIO() = mNetworkIO
    fun mainThread() = mMainThread

    companion object {
        private class MainThreadExecutor : Executor {
            private val mMainThreadHandler = Handler(Looper.getMainLooper())
            override fun execute(@NonNull command: Runnable) {
                mMainThreadHandler.post(command)
            }
        }
    }
}