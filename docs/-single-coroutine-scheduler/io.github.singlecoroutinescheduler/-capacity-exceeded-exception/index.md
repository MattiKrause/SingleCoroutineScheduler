[SingleCoroutineScheduler](../../index.md) / [io.github.singlecoroutinescheduler](../index.md) / [CapacityExceededException](./index.md)

# CapacityExceededException

(JVM) `class CapacityExceededException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html)

This class can be thrown by [SingleCoroutineScheduler](../-single-coroutine-scheduler/index.md), specifically by [SingleCoroutineScheduler.scheduleOrThrow](../-single-coroutine-scheduler/schedule-or-throw.md)

### Parameters

`name` - The name of the [SingleCoroutineScheduler](../-single-coroutine-scheduler/index.md) and thus its worker coroutine

`capacity` - The maximum capacity of the throwing scheduler

**Author**
Matti Krause

### Properties

| Name | Summary |
|---|---|
| (JVM) [capacity](capacity.md) | The maximum capacity of the throwing scheduler`val capacity: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| (JVM) [message](message.md) | `val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| (JVM) [name](name.md) | The name of the [SingleCoroutineScheduler](../-single-coroutine-scheduler/index.md) and thus its worker coroutine`val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
