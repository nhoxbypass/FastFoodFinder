package com.iceteaviet.fastfoodfinder.utils.rx

import io.reactivex.Scheduler

/**
 * Created by tom on 2019-06-02.
 */
interface SchedulerProvider {
    fun ui(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler
}