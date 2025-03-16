package com.iceteaviet.fastfoodfinder.core.worker

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Created by nhoxbypass on 20/04/2023.
 */
object ComputationWorker {
    private val Computation = Dispatchers.Default

    private val exceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            // TODO: Print logs
        }
    }
    private val coroutineContext by lazy {
        SupervisorJob() + Computation + exceptionHandler
    }
    val scope by lazy {
        object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = this@ComputationWorker.coroutineContext
        }
    }

    fun cancelAll() {
        scope.cancel()
    }
}