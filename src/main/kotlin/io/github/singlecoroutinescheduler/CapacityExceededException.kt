package io.github.singlecoroutinescheduler

/**
 * This class can be thrown by [SingleCoroutineScheduler], specifically by [SingleCoroutineScheduler.scheduleOrThrow]
 *
 * @param name The name of the [SingleCoroutineScheduler] and thus its worker coroutine
 * @param capacity The maximum capacity of the throwing scheduler
 *
 * @author Matti Krause
 */
class CapacityExceededException internal constructor(
    val name: String,
    val capacity: Int
) : Exception("The SingleCoroutineScheduler $name exceeded its capacity of $capacity") {
    override val message: String
        get() = super.message!!
}