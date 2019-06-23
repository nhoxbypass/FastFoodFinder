package com.iceteaviet.fastfoodfinder.service.eventbus.core

import org.greenrobot.eventbus.EventBus

/**
 * Created by tom on 2019-06-23.
 */
class RobotBus(private val bus: EventBus) : IBus {

    override fun post(event: Event) {
        bus.post(event)
    }

    override fun register(subscriber: Any) {
        bus.register(subscriber)
    }

    override fun unregister(subscriber: Any) {
        bus.unregister(subscriber)
    }
}