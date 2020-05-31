[SingleCoroutineScheduler](../index.md) / [io.github.singlecoroutinescheduler](./index.md)

## Package io.github.singlecoroutinescheduler

### Types

| Name | Summary |
|---|---|
| (JVM) [SingleCoroutineScheduler](-single-coroutine-scheduler/index.md) | This [reactor.core.scheduler](#) runs on a single kotlinx coroutine. This means that is class is NOT anticipated for large amounts of work, it is rather used to synchronize different subscribers on different publishers, while having a small footprint when not used. This class also supports delayed schedule and periodical schedule. For these purposes, new coroutines are spawned, they are also cancelled in case this scheduler is cancelled. The initialization of the initial coroutine is executed lazily, meaning it is only started when [start](-single-coroutine-scheduler/start.md) is called or when work is coming in. The optional parameter parentJob determines to which [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html) the lifecycle of the new scheduler is bound. If the parent cancels, the scheduler cancels too.`class SingleCoroutineScheduler : `[`Scheduler`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/scheduler/Scheduler.html) |

### Exceptions

| Name | Summary |
|---|---|
| (JVM) [CapacityExceededException](-capacity-exceeded-exception/index.md) | This class can be thrown by [SingleCoroutineScheduler](-single-coroutine-scheduler/index.md), specifically by [SingleCoroutineScheduler.scheduleOrThrow](-single-coroutine-scheduler/schedule-or-throw.md)`class CapacityExceededException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |

### Extensions for External Classes

| Name | Summary |
|---|---|
| (JVM) [kotlinx.coroutines.CoroutineScope](kotlinx.coroutines.-coroutine-scope/index.md) |  |
