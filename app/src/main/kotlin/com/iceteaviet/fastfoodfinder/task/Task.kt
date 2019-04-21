package com.iceteaviet.fastfoodfinder.task

/**
 * Created by tom on 12/26/18.
 *
 * @see com.iceteaviet.fastfoodfinder.task.worker.AppTaskBackgroundWorker
 */
@Deprecated("See AppTaskBackgroundWorker")
interface Task {
    fun doTask()
    fun isImportant(): Boolean
}