package io.github.singlecoroutinescheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel

/**
 * This Methods calls the constructor of [SingleCoroutineScheduler] with the receiver as launch scope.
 *
 * @receiver The launch scope for the new [SingleCoroutineScheduler]
 *
 * @see SingleCoroutineScheduler
 *
 * @author Matti Krause
 */
fun CoroutineScope.singleCoroutineScheduler(
    bufferCapacity: Int = Channel.UNLIMITED,
    parentJob: Job? = this.coroutineContext[Job],
    name: String? = null,
    throwOnCapacityExceeded: Boolean = false
): SingleCoroutineScheduler = SingleCoroutineScheduler(
    bufferCapacity = bufferCapacity,
    launchScope = this,
    parentJob = parentJob,
    name = name,
    throwOnCapacityExceeded = throwOnCapacityExceeded
)