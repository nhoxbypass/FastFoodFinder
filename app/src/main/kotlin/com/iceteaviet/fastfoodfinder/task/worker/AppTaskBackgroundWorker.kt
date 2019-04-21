package com.iceteaviet.fastfoodfinder.task.worker

import com.iceteaviet.fastfoodfinder.task.Task
import com.iceteaviet.fastfoodfinder.utils.d

/**
 * Created by tom on 12/26/18.
 *
 * @see Task
 */

@Deprecated(message = "This type of background worker thread only suitable for Java", replaceWith = ReplaceWith("Kotlin Coroutines or WorkManager instead"))
class AppTaskBackgroundWorker : Thread("AppTaskBackgroundWorker") {
    companion object {
        const val TAG = "AppTaskBackgroundWorker"
        var queue: ArrayList<Task> = ArrayList()
        var worker: AppTaskBackgroundWorker? = null
        @Volatile
        var running: Boolean = true

        @Synchronized
        fun startWaitingForTasks() {
            if (worker == null) {
                synchronized(AppTaskBackgroundWorker::class.java) {
                    if (worker == null) {
                        worker = AppTaskBackgroundWorker()
                    }
                }
            }
        }

        @Synchronized
        fun queueTask(task: Task) {
            startWaitingForTasks()
            if (worker != null) {
                synchronized(worker!!) {
                    if (task.isImportant()) {
                        queue.add(0, task)
                    } else {
                        queue.add(task)
                    }
                    (worker as java.lang.Object).notify()
                }
            }
        }

        fun isEmpty(): Boolean {
            if (worker == null)
                return true
            synchronized(worker!!) {
                return queue.isEmpty()
            }
        }

        fun isEmptyQueueTask(): Boolean {
            return queue.isEmpty()
        }

        fun stopThread() {
            if (worker != null) {
                synchronized(worker!!) {
                    while (!queue.isEmpty()) {
                        worker!!.doTask()
                    }

                    running = false
                    (worker as java.lang.Object).notify()
                }

                if (worker != null)
                    worker!!.interrupt()
            }
        }
    }

    init {
        if (worker == null) {
            worker = this
            start()
        }
    }

    override fun run() {
        while (running) {
            synchronized(this) {
                if (queue.isEmpty()) {
                    d("Waiting for new task request...")
                    try {
                        (worker as java.lang.Object).wait()
                    } catch (ex: Exception) {
                    }
                }
            }

            if (!running)
                break

            doTask()
        }
    }

    private fun doTask() {
        try {
            queue.removeAt(0).doTask()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}