package com.iceteaviet.fastfoodfinder.utils.rx

import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler


/**
 * Created by tom on 2019-06-02.
 */
class TestSchedulerProvider(private val testScheduler: TestScheduler) : SchedulerProvider {
    override fun ui(): Scheduler {
        return testScheduler
    }

    override fun computation(): Scheduler {
        return testScheduler
    }

    override fun io(): Scheduler {
        return testScheduler
    }
}