package com.icongkhanh.kmuzic.data.executor

import java.util.concurrent.*

object ThreadPoolManager {

    private const val DEFAULT_THREAD_POOL_SIZE = 1
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private const val KEEP_ALIVE_TIME = 1L
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

    private val taskQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    private val threadPoolExecutor: ExecutorService = ThreadPoolExecutor(
        DEFAULT_THREAD_POOL_SIZE,
        1,
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        taskQueue
    )

    fun addTask(task: () -> Unit) {
        threadPoolExecutor.submit(task)
    }
}
