package com.iceteaviet.fastfoodfinder.task

/**
 * Created by tom on 12/26/18.
 */
interface Task {
    fun doTask()
    fun isImportant(): Boolean
}