package io.github.singlecoroutinescheduler

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import reactor.core.Disposable
import reactor.core.scheduler.Scheduler
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * This [reactor.core.scheduler] runs on a single kotlinx coroutine. This means that is class is NOT anticipated for
 * large amounts of work, it is rather used to synchronize different subscribers on different publishers, while having
 * a small footprint when not used. This class also supports delayed schedule and periodical schedule. For these purposes,
 * new coroutines are spawned, they are also cancelled in case this scheduler is cancelled. The initialization of the
 * initial coroutine is executed lazily, meaning it is only started when [start] is called or when work is coming in.
 * The optional parameter parentJob determines to which [Job] the lifecycle of the new scheduler is bound. If the parent
 * cancels, the scheduler cancels too.
 *
 * @param bufferCapacity The [capacity][Channel.UNLIMITED] of the underlying [Channel]
 * @param launchScope The scope in which the worker and helping coroutines are launched.
 * @param parentJob If parentJob cancels, the Instances coroutine cancels too. It is per default the Job of the [launchScope]
 * @param name The name of the worker coroutine
 * @param throwOnCapacityExceeded If this is true than a [CapacityExceededException] is thrown in [schedule] rather than blocking
 * @param ignoreExceptions If this is true, then exceptions won't crash the worker coroutine,
 * meaning that also it won't crash the parent job.
 *
 * @author Matti Krause
 */
class SingleCoroutineScheduler(
    private val bufferCapacity: Int = Channel.UNLIMITED,
    private val launchScope: CoroutineScope = GlobalScope,
    private val parentJob: Job? = launchScope.coroutineContext[Job],
    name: String? = null,
    private val throwOnCapacityExceeded: Boolean = false,
    private val ignoreExceptions: Boolean = true
) : Scheduler {
    private class SchedulerWorkerFacade(private val parent: SingleCoroutineScheduler) : Scheduler.Worker {
        override fun schedule(task: Runnable): Disposable {
            return parent.schedule(task)
        }

        override fun dispose() {
        }

    }

    private class Work(
        private val task: Runnable,
        private val ignoreExceptions: Boolean
    ) : Disposable {
        private var doWork: Boolean = true
        fun execute() {
            if (doWork) {
                if (ignoreExceptions)
                    try {
                        task.run()
                    } catch (ignored: Throwable) {
                    }
                else
                    task.run()
            }
        }

        override fun dispose() {
            doWork = false
        }

        override fun isDisposed(): Boolean = !doWork
    }

    private companion object {
        val workerCounter = AtomicInteger(0)
    }

    private val name: String = name ?: "SingleCoroutineScheduler${workerCounter.incrementAndGet()}"

    private var workerJobLazy: Job? = null
        set(value) {
            if (field != null)
                throw IllegalStateException("Property is already initialized")
            field = value
        }
    private val workerJob: Job
        get() {
            if (workerJobLazy == null)
                start()
            return workerJobLazy!!
        }


    private var workerChannelLazy: Channel<Work>? = null
        set(value) {
            if (field != null)
                throw IllegalStateException("Property is already initialized")
            field = value
        }
    private val workerChannel: Channel<Work>
        get() {
            if (workerChannelLazy == null)
                start()
            return workerChannelLazy!!
        }


    private lateinit var context: CoroutineContext

    private val subroutineCounter = AtomicInteger(0)

    /**
     * Initializes this scheduler and starts the worker coroutine. If the scheduler is already running, nothing happens
     */
    override fun start() {
        if (workerJobLazy == null) {
            context = EmptyCoroutineContext + Job(parent = parentJob)
            workerChannelLazy = Channel(bufferCapacity)
            workerJobLazy = launchScope.launch(context + CoroutineName(name)) {
                try {
                    while (isActive)
                        workerChannel.receive().execute()
                } finally {
                    workerChannel.cancel()
                }
            }
        }
    }

    /**
     * Adds a task for the coroutine worker. If [throwOnCapacityExceeded] is true, then [scheduleOrThrow] is used, otherwise
     * [scheduleBlocking] is used.
     *
     * @param task The task that is to be executed.
     *
     * @return A Disposable, which can be used to prevent execution of the task.
     * Note that the canceled task is not removed from the buffer
     *
     * @see scheduleBlocking
     * @see scheduleOrThrow
     */
    override fun schedule(task: Runnable): Disposable {
        return if (throwOnCapacityExceeded)
            scheduleOrThrow(task)
        else
            scheduleBlocking(task)
    }

    /**
     * Adapter from kotlin lambda to Java Runnable
     *
     * @see schedule
     */
    inline fun schedule(crossinline task: () -> Unit): Disposable {
        return schedule(Runnable(task))
    }

    /**
     * Adds a task for the coroutine worker. If the buffer capacity is exceeded, this method suspends.
     *
     * @param task The task that is to be executed
     *
     * @return A Disposable, which can be used to prevent execution of the task.
     * Note that the canceled task is not removed from the buffer
     */
    suspend fun scheduleSuspending(task: Runnable): Disposable {
        val work = Work(task, ignoreExceptions)
        workerChannel.send(work)
        return work
    }

    /**
     * Adapter from kotlin lambda to Java Runnable
     *
     * @see scheduleSuspending
     */
    suspend inline fun scheduleSuspending(crossinline task: () -> Unit): Disposable {
        return scheduleSuspending(Runnable(task))
    }

    /**
     * Adds a task for the coroutine worker. If the buffer capacity is exceeded, this method blocks the caller process.
     *
     * @param task The task that is to be executed
     *
     * @return A Disposable, which can be used to prevent execution of the task.
     * Note that the canceled task is not removed from the buffer
     */
    fun scheduleBlocking(task: Runnable): Disposable {
        val work = Work(task, ignoreExceptions)
        workerChannel.sendBlocking(work)
        return work
    }

    /**
     * Adapter from kotlin lambda to Java Runnable
     *
     * @see scheduleBlocking
     */
    inline fun scheduleBlocking(crossinline task: () -> Unit): Disposable {
        return scheduleBlocking(Runnable(task))
    }

    /**
     * Adds a task for the coroutine worker. If the buffer capacity is exceeded, this method throws a
     * [CapacityExceededException] exception
     *
     * @param task THe task that is to be executed
     *
     * @return A Disposable, which can be used to prevent execution of the task.
     * Note that the canceled task is not removed from the buffer
     *
     * @throws CapacityExceededException Throws when the buffer capacity is exceeded
     */
    fun scheduleOrThrow(task: Runnable): Disposable {
        val work = Work(task, ignoreExceptions)
        if (!workerChannel.offer(work))
            throw CapacityExceededException(name, bufferCapacity)
        return work
    }

    /**
     * Adapter from kotlin lambda to Java Runnable
     *
     * @see scheduleOrThrow
     */
    inline fun scheduleOrThrow(crossinline task: () -> Unit): Disposable {
        return scheduleOrThrow(Runnable(task))
    }

    /**
     * Adds a task for the coroutine worker. If the buffer capacity is exceeded, null is returned
     *
     * @param task The task that is to be executed
     *
     * @return A Disposable, which can be used to prevent execution of the task.
     * Note that the canceled task is not removed from the buffer
     */
    fun trySchedule(task: Runnable): Disposable? {
        val work = Work(task, ignoreExceptions)
        return if (workerChannel.offer(work)) work else null
    }

    /**
     * Adapter from kotlin lambda to Java Runnable
     *
     * @see trySchedule
     */
    inline fun trySchedule(crossinline task: () -> Unit): Disposable? {
        return trySchedule(Runnable(task))
    }

    /**
     * Adds a task for the coroutine worker after a given delay.
     *
     * This method spawns a new coroutine. If the buffer capacity is exceeded after the delay, the new coroutine suspends.
     * This method can only delay in millie seconds.
     *
     * ```
     * schedule(task = ..., delay = 1, unit = TimeUnit.MILLISECONDS)
     * ```
     * Will delay 1 ms
     *
     * ```
     * schedule(task=..., delay = 500, unit = TimeUnit.MICROSECONDS)
     * ```
     * Will delay 0 ms
     *
     * ```
     * schedule(task=..., delay = 5000, unit = TimeUnit.MICROSECONDS)
     * ```
     * Will delay 5 ms
     *
     *
     * @param task The task that is to be executed after the delay.
     * @param delay The amount of delayed time before scheduling.
     * @param unit The [TimeUnit] of [delay]
     *
     * @return A Disposable, which can be used to prevent execution of the task.
     * Note that the canceled task is not removed from the buffer. If the delaying coroutine is still running
     * at the time disposal, it is cancelled
     */
    override fun schedule(task: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val delayInMs: Long =
            if (unit != TimeUnit.MILLISECONDS)
                unit.toMillis(delay)
            else
                delay


        return if (delayInMs == 0L)
            schedule(task)
        else {
            var work: Work? = null
            val job: Job = subroutine("$name-delayed-work-${subroutineCounter.incrementAndGet()}") {
                delay(delayInMs)
                if (isActive) {
                    work = Work(task, ignoreExceptions)
                    workerChannel.send(work!!)
                }
            }
            Disposable {
                job.cancel()
                work?.dispose()
            }
        }
    }

    /**
     * Adapter from kotlin lambda to Java Runnable
     *
     * [see: original schedule with delay][schedule]
     */
    fun schedule(delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS, task: () -> Unit): Disposable {
        return schedule(task, delay, unit)
    }

    /**
     * Adds a task for the coroutine worker every [delay].
     * This task spawns a new coroutine, that only adds the task and does not execute it.
     * The delay in between executions is not exact. It can take longer when the coroutine is busy working
     * It can only delay in millie seconds, if your delay in ms is less than one it will be ignored:
     *  ```
     * schedule(task = ..., delay = 1, unit = TimeUnit.MILLISECONDS)
     * ```
     * Will delay 1 ms
     *
     * ```
     * schedule(task=..., delay = 500, unit = TimeUnit.MICROSECONDS)
     * ```
     * Will delay 0 ms
     *
     * ```
     * schedule(task=..., delay = 5000, unit = TimeUnit.MICROSECONDS)
     * ```
     * Will delay 5 ms
     *
     * @param task The task that is to be executed repeatedly
     * @param initialDelay The delay before the first execution of [task]
     * @param period delay between each execution of [task]
     * @param unit The time unit of [initialDelay] and [period]
     *
     * @return Disposable, which can be used to prevent execution of the task.
     * Note that the canceled tasks are not removed from the buffer. The periodically scheduling coroutine is cancelled.
     */
    override fun schedulePeriodically(
        task: Runnable,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ): Disposable {
        val delayInMs: Long =
            when {
                initialDelay == 0L -> 0
                unit != TimeUnit.MILLISECONDS -> unit.toMillis(initialDelay)
                else -> initialDelay
            }
        val periodInMs: Long =
            if (unit != TimeUnit.MILLISECONDS)
                unit.toMillis(period)
            else
                period

        var currentWork: Work? = null
        val job: Job = subroutine("$name-periodically-worker-${subroutineCounter.incrementAndGet()}") {
            if (delayInMs > 0)
                delay(delayInMs)
            while (isActive) {
                currentWork = Work(task, ignoreExceptions)
                workerChannel.send(currentWork!!)
                delay(periodInMs)
            }
        }

        return Disposable {
            job.cancel()
            currentWork?.dispose()
        }
    }

    /**
     * Adapter from kotlin lambda to Java Runnable
     *
     * [see: original schedule with delay][schedule]
     */
    fun schedulePeriodically(
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        task: () -> Unit
    ): Disposable {
        return schedulePeriodically(task, initialDelay, delay, unit)
    }

    /**
     * Returns a worker facade delegating to [schedule]
     *
     * @return A worker facade delegating to [schedule]
     */
    override fun createWorker(): Scheduler.Worker = SchedulerWorkerFacade(this)

    private fun subroutine(name: String, func: suspend CoroutineScope.() -> Unit): Job {
        workerJob//worker job calls start if not done before, to initialize context
        return launchScope.launch(context = context + CoroutineName(name), block = func)
    }

    /**
     * This method disposes all resources held by this Instance, including the worker and all delaying and periodically
     * scheduling coroutines.
     */
    override fun dispose() {
        if (workerJobLazy != null) {
            context.cancel()
            workerChannel.cancel()
        }
    }

    /**
     * Returns whether the working coroutine has been cancelled.
     */
    override fun isDisposed(): Boolean {
        return workerJobLazy != null && !workerJob.isActive
    }
}