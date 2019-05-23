@file:JvmName("AppLogger")

package com.iceteaviet.fastfoodfinder.utils

import com.iceteaviet.fastfoodfinder.BuildConfig

import timber.log.Timber

/**
 * Created by tom on 8/7/18.
 */

/**
 * Init Timber only in debug mode
 */
fun initLogger() {
    if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
    }
}

/**
 * Log debug info
 */
fun d(s: String, vararg objects: Any) {
    Timber.d(s, objects)
}

/**
 * Log debug exception
 */
fun d(throwable: Throwable, s: String, vararg objects: Any) {
    Timber.d(throwable, s, objects)
}

/**
 * Log info message
 */
fun i(s: String, vararg objects: Any) {
    Timber.i(s, objects)
}

/**
 * Log an info exception and a message
 */
fun i(throwable: Throwable, s: String, vararg objects: Any) {
    Timber.i(throwable, s, objects)
}

/**
 * Log a warning message
 */
fun w(s: String, vararg objects: Any) {
    Timber.w(s, objects)
}

/**
 * Log a warning exception and a message
 */
fun w(throwable: Throwable, s: String, vararg objects: Any) {
    Timber.w(throwable, s, objects)
}

/**
 * Log an error message
 */
fun e(s: String, vararg objects: Any) {
    Timber.e(s, objects)
}

/**
 * Log an error exception and a message
 */
fun e(throwable: Throwable, s: String, vararg objects: Any) {
    Timber.e(throwable, s, objects)
}

/**
 * Log an assert message
 */
fun wtf(s: String, vararg objects: Any) {
    Timber.wtf(s, objects)
}

/**
 * Log an assert exception and a message
 */
fun wtf(throwable: Throwable, s: String, vararg objects: Any) {
    Timber.wtf(throwable, s, objects)
}