

### All Types

| Name | Summary |
|---|---|
|(JVM)

##### [io.github.singlecoroutinescheduler.CapacityExceededException](../io.github.singlecoroutinescheduler/-capacity-exceeded-exception/index.md)

This class can be thrown by [SingleCoroutineScheduler](../io.github.singlecoroutinescheduler/-single-coroutine-scheduler/index.md), specifically by [SingleCoroutineScheduler.scheduleOrThrow](../io.github.singlecoroutinescheduler/-single-coroutine-scheduler/schedule-or-throw.md)


|(JVM)  (extensions in package io.github.singlecoroutinescheduler)

##### [kotlinx.coroutines.CoroutineScope](../io.github.singlecoroutinescheduler/kotlinx.coroutines.-coroutine-scope/index.md)


|(JVM)

##### [io.github.singlecoroutinescheduler.SingleCoroutineScheduler](../io.github.singlecoroutinescheduler/-single-coroutine-scheduler/index.md)

This [reactor.core.scheduler](#) runs on a single kotlinx coroutine. This means that is class is NOT anticipated for
large amounts of work, it is rather used to synchronize different subscribers on different publishers, while having
a small footprint when not used. This class also supports delayed schedule and periodical schedule. For these purposes,
new coroutines are spawned, they are also cancelled in case this scheduler is cancelled. The initialization of the
initial coroutine is executed lazily, meaning it is only started when [start](../io.github.singlecoroutinescheduler/-single-coroutine-scheduler/start.md) is called or when work is coming in.
The optional parameter parentJob determines to which [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html) the lifecycle of the new scheduler is bound. If the parent
cancels, the scheduler cancels too.


