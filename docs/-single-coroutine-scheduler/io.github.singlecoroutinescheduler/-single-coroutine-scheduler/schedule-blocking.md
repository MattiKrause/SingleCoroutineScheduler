[SingleCoroutineScheduler](../../index.md) / [io.github.singlecoroutinescheduler](../index.md) / [SingleCoroutineScheduler](index.md) / [scheduleBlocking](./schedule-blocking.md)

# scheduleBlocking

(JVM) `fun scheduleBlocking(task: `[`Runnable`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-runnable.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adds a task for the coroutine worker. If the buffer capacity is exceeded, this method blocks the caller process.

### Parameters

`task` - The task that is to be executed

**Return**
A Disposable, which can be used to prevent execution of the task.
Note that the canceled task is not removed from the buffer

(JVM) `inline fun scheduleBlocking(crossinline task: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Disposable`](https://projectreactor.io/docs/core/3.3.5.RELEASE/api/reactor/core/Disposable.html)

Adapter from kotlin lambda to Java Runnable

**See Also**

[scheduleBlocking](./schedule-blocking.md)

