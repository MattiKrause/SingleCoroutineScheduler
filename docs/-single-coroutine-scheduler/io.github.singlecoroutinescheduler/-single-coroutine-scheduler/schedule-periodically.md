[SingleCoroutineScheduler](../../index.md) / [io.github.singlecoroutinescheduler](../index.md) / [SingleCoroutineScheduler](index.md) / [schedulePeriodically](./schedule-periodically.md)

# schedulePeriodically

(JVM) `fun schedulePeriodically(task: `[`Runnable`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-runnable.html)`, initialDelay: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, period: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, unit: `[`TimeUnit`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adds a task for the coroutine worker every [delay](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/delay.html).
This task spawns a new coroutine, that only adds the task and does not execute it.
The delay in between executions is not exact. It can take longer when the coroutine is busy working
It can only delay in millie seconds, if your delay in ms is less than one it will be ignored:

```
schedule(task = ..., delay = 1, unit = TimeUnit.MILLISECONDS)
```

Will delay 1 ms

```
schedule(task=..., delay = 500, unit = TimeUnit.MICROSECONDS)
```

Will delay 0 ms

```
schedule(task=..., delay = 5000, unit = TimeUnit.MICROSECONDS)
```

Will delay 5 ms

### Parameters

`task` - The task that is to be executed repeatedly

`initialDelay` - The delay before the first execution of [task](schedule-periodically.md#io.github.singlecoroutinescheduler.SingleCoroutineScheduler$schedulePeriodically(java.lang.Runnable, kotlin.Long, kotlin.Long, java.util.concurrent.TimeUnit)/task)

`period` - delay between each execution of [task](schedule-periodically.md#io.github.singlecoroutinescheduler.SingleCoroutineScheduler$schedulePeriodically(java.lang.Runnable, kotlin.Long, kotlin.Long, java.util.concurrent.TimeUnit)/task)

`unit` - The time unit of [initialDelay](schedule-periodically.md#io.github.singlecoroutinescheduler.SingleCoroutineScheduler$schedulePeriodically(java.lang.Runnable, kotlin.Long, kotlin.Long, java.util.concurrent.TimeUnit)/initialDelay) and [period](schedule-periodically.md#io.github.singlecoroutinescheduler.SingleCoroutineScheduler$schedulePeriodically(java.lang.Runnable, kotlin.Long, kotlin.Long, java.util.concurrent.TimeUnit)/period)

**Return**
Disposable, which can be used to prevent execution of the task.
Note that the canceled tasks are not removed from the buffer. The periodically scheduling coroutine is cancelled.

(JVM) `fun schedulePeriodically(initialDelay: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, delay: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, unit: `[`TimeUnit`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html)` = TimeUnit.MILLISECONDS, task: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adapter from kotlin lambda to Java Runnable

[see: original schedule with delay](schedule.md)

