package com.iceteaviet.fastfoodfinder.core.worker

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Created by nhoxbypass on 20/04/2023.
 */
@OptIn(ExperimentalCoroutinesApi::class)
object SerialWorker {
    private val SingleThreadIO = Dispatchers.IO.limitedParallelism(1)

    private val exceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            // TODO: Print logs
        }
    }
    private val coroutineContext by lazy {
        SupervisorJob() + SingleThreadIO + exceptionHandler
    }
    val scope by lazy {
        object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = this@SerialWorker.coroutineContext
        }
    }

    fun cancelAll() {
        scope.cancel()
    }
}