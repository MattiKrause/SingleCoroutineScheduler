[SingleCoroutineScheduler](../../index.md) / [io.github.singlecoroutinescheduler](../index.md) / [SingleCoroutineScheduler](index.md) / [schedule](./schedule.md)

# schedule

(JVM) `fun schedule(task: `[`Runnable`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-runnable.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adds a task for the coroutine worker. If [throwOnCapacityExceeded](#) is true, then [scheduleOrThrow](schedule-or-throw.md) is used, otherwise
[scheduleBlocking](schedule-blocking.md) is used.

### Parameters

`task` - The task that is to be executed.

**Return**
A Disposable, which can be used to prevent execution of the task.
Note that the canceled task is not removed from the buffer

**See Also**

[scheduleBlocking](schedule-blocking.md)

[scheduleOrThrow](schedule-or-throw.md)

(JVM) `inline fun schedule(crossinline task: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adapter from kotlin lambda to Java Runnable

**See Also**

[schedule](./schedule.md)

(JVM) `fun schedule(task: `[`Runnable`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-runnable.html)`, delay: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, unit: `[`TimeUnit`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adds a task for the coroutine worker after a given delay.

This method spawns a new coroutine. If the buffer capacity is exceeded after the delay, the new coroutine suspends.
This method can only delay in millie seconds.

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

`task` - The task that is to be executed after the delay.

`delay` - The amount of delayed time before scheduling.

`unit` - The [TimeUnit](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) of [delay](schedule.md#io.github.singlecoroutinescheduler.SingleCoroutineScheduler$schedule(java.lang.Runnable, kotlin.Long, java.util.concurrent.TimeUnit)/delay)

**Return**
A Disposable, which can be used to prevent execution of the task.
Note that the canceled task is not removed from the buffer. If the delaying coroutine is still running
at the time disposal, it is cancelled

(JVM) `fun schedule(delay: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, unit: `[`TimeUnit`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html)` = TimeUnit.MILLISECONDS, task: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adapter from kotlin lambda to Java Runnable

[see: original schedule with delay](./schedule.md)

