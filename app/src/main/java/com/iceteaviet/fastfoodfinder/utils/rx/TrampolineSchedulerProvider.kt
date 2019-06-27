package com.iceteaviet.fastfoodfinder.utils.rx

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


/**
 * Created by tom on 2019-06-02.
 */
class TrampolineSchedulerProvider : SchedulerProvider {
    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }
}