package io.github.singlecoroutinescheduler

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.Disposable
import java.lang.Runnable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.concurrent.thread

class SingleCoroutineSchedulerTest {
    private fun noCapacityScheduler() = SingleCoroutineScheduler(bufferCapacity = Channel.RENDEZVOUS)
    private fun blockedScheduler(): SingleCoroutineScheduler {
        return noCapacityScheduler().apply {
            schedule { runBlocking { delay(1000) } }
        }
    }

    @Test
    fun testLifecycleParentChild() {
        val job = GlobalScope.launch {
            delay(Long.MAX_VALUE)
        }
        val scheduler = GlobalScope.singleCoroutineScheduler(
            parentJob = job
        )
        scheduler.start()
        job.cancel()
        Thread.sleep(25)
        Assertions.assertTrue(scheduler.isDisposed)


    }

    @Test
    fun testLifecycleChildParent() {
        val job = GlobalScope.launch {
            delay(Long.MAX_VALUE)
        }
        val scheduler = GlobalScope.singleCoroutineScheduler(
            parentJob = job
        )
        scheduler.dispose()
        Thread.sleep(25)
        Assertions.assertTrue(job.isActive)
    }

    @Test
    fun testDisposed() {
        val scheduler = SingleCoroutineScheduler()
        scheduler.start()
        scheduler.dispose()
        Thread.sleep(25)
        Assertions.assertTrue(scheduler.isDisposed)
    }

    @Test
    fun testSchedule() {
        var taskCalled = false
        val scheduler = SingleCoroutineScheduler()
        scheduler.schedule {
            taskCalled = true
        }
        Thread.sleep(25)
        Assertions.assertTrue(taskCalled)
    }

    @Test
    fun testScheduleDisposal() {
        var taskCalled = false
        val scheduler = SingleCoroutineScheduler()
        scheduler.schedule { runBlocking { delay(25L) } }
        scheduler.schedule { taskCalled = true }.dispose()
        Thread.sleep(50L)
        Assertions.assertFalse(taskCalled)
        scheduler.dispose()
    }

    @Test
    fun testScheduleBlocking() {
        val countDown = CountDownLatch(1)
        val scheduler = blockedScheduler()

        thread { scheduler.scheduleBlocking(Runnable { }); countDown.countDown() }
        Thread.sleep(25)
        Assertions.assertTrue { countDown.count != 0L }
        scheduler.dispose()
    }

    @Test
    fun testScheduleThrows() {
        val scheduler = blockedScheduler()
        Assertions.assertThrows(CapacityExceededException::class.java) {
            scheduler.scheduleOrThrow(Runnable { })
        }
        scheduler.dispose()
    }

    @Test
    fun testScheduleTry() {
        val scheduler = blockedScheduler()
        Assertions.assertNull(scheduler.trySchedule {})
        scheduler.dispose()
    }

    @Test
    fun testScheduleSuspending() {
        val scheduler = blockedScheduler()
        Assertions.assertThrows(TimeoutException::class.java) {
            runBlocking {
                withTimeout(50) {
                    try {
                        scheduler.scheduleSuspending {}
                    } catch (e: TimeoutCancellationException) {
                        throw TimeoutException()
                    }
                }
            }
        }
        scheduler.dispose()
    }

    @Test
    fun testDelay() {
        var called = false
        val scheduler = SingleCoroutineScheduler()
        scheduler.schedule(delay = 100, unit = TimeUnit.MILLISECONDS) {
            called = true
        }
        Thread.sleep(50)
        Assertions.assertFalse(called)
        Thread.sleep(150)
        Assertions.assertTrue(called)
        scheduler.dispose()
    }

    @Test
    fun testDelayDispose() {
        var called = false
        val scheduler = SingleCoroutineScheduler()
        scheduler.schedule(100) { called = true }.dispose()
        Thread.sleep(150)
        Assertions.assertFalse(called)
        scheduler.dispose()
    }

    // can fail from time to time; run multiple times
    @Test
    fun testSchedulePeriodically() {
        var i = 0
        val scheduler = SingleCoroutineScheduler()
        scheduler.schedulePeriodically(
            initialDelay = 0,
            delay = 100L
        ) {
            i++
        }
        Thread.sleep(450)
        Assertions.assertEquals(4, i)
        scheduler.dispose()
    }

    @Test
    fun testScheduleDispose() {
        var i = 0
        val scheduler = SingleCoroutineScheduler()
        val task: Disposable = scheduler.schedulePeriodically(
            initialDelay = 0,
            delay = 100L
        ) {
            i++
        }
        Thread.sleep(250)
        task.dispose()
        Thread.sleep(200)
        Assertions.assertEquals(3, i)
        scheduler.dispose()
    }

    @Test
    fun testScheduleDelay() {
        var i = 0
        val scheduler = SingleCoroutineScheduler()
        scheduler.schedulePeriodically(initialDelay = 100, delay = 100) {
            i++
        }
        Thread.sleep(50)
        Assertions.assertEquals(i, 0)
        Thread.sleep(100)
        Assertions.assertEquals(i, 1)
        scheduler.dispose()
    }

    @Test
    fun testExceptionEscalation() {
        val parent: Job = GlobalScope.launch { delay(Long.MAX_VALUE) }
        val scheduler = SingleCoroutineScheduler(parentJob = parent, ignoreExceptions = false)
        scheduler.schedule { throw Exception() }
        Thread.sleep(500)
        Assertions.assertFalse(parent.isActive)
        scheduler.dispose()
    }
}
