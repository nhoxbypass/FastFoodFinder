package com.iceteaviet.fastfoodfinder.service.eventbus.core

/**
 * Created by tom on 2019-06-23.
 */
interface IBus {
    fun post(event: Event)
    fun register(subscriber: Any)
    fun unregister(subscriber: Any)
}