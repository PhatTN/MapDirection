package com.example.phattn.mapdirection.util

import android.os.SystemClock
import android.util.ArrayMap
import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RateLimiter<in KEY>(timeout: Long, timeUnit: TimeUnit) {
    private val mTimestamps = ArrayMap<KEY, Long>()
    private val mTimeout = timeUnit.toMillis(timeout)

    @Synchronized fun shouldFetch(key: KEY) : Boolean {
        val lastFetched = mTimestamps[key]
        val now = now()
        if (lastFetched == null) {
            mTimestamps.put(key, now)
            return true
        }

        if (now - lastFetched > mTimeout) {
            mTimestamps.put(key, now)
            return true
        }
        return false
    }

    private fun now() = SystemClock.uptimeMillis()
    @Synchronized fun reset(key: KEY) = mTimestamps.remove(key)
}