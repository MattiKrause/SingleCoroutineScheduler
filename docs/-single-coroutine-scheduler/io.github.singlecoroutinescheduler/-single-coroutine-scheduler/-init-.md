[SingleCoroutineScheduler](../../index.md) / [io.github.singlecoroutinescheduler](../index.md) / [SingleCoroutineScheduler](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

(JVM) `SingleCoroutineScheduler(bufferCapacity: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = Channel.UNLIMITED, launchScope: `[`CoroutineScope`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html)` = GlobalScope, parentJob: `[`Job`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html)`? = launchScope.coroutineContext[Job], dispatcher: `[`CoroutineDispatcher`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/index.html)` = Dispatchers.Default, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, throwOnCapacityExceeded: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, ignoreExceptions: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true)`

This [reactor.core.scheduler](#) runs on a single kotlinx coroutine. This means that is class is NOT anticipated for
large amounts of work, it is rather used to synchronize different subscribers on different publishers, while having
a small footprint when not used. This class also supports delayed schedule and periodical schedule. For these purposes,
new coroutines are spawned, they are also cancelled in case this scheduler is cancelled. The initialization of the
initial coroutine is executed lazily, meaning it is only started when [start](start.md) is called or when work is coming in.
The optional parameter parentJob determines to which [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html) the lifecycle of the new scheduler is bound. If the parent
cancels, the scheduler cancels too.

### Parameters

`bufferCapacity` - The [capacity](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel/-factory/-u-n-l-i-m-i-t-e-d.html) of the underlying [Channel](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel/index.html)

`launchScope` - The scope in which the worker and helping coroutines are launched.

`parentJob` - If parentJob cancels, the Instances coroutine cancels too. It is per default the Job of the [launchScope](#)

`dispatcher` - The [CoroutineDispatcher](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/index.html) of the launched coroutines.

`name` - The name of the worker coroutine

`throwOnCapacityExceeded` - If this is true than a [CapacityExceededException](../-capacity-exceeded-exception/index.md) is thrown in [schedule](schedule.md) rather than blocking

`ignoreExceptions` - If this is true, then exceptions won't crash the worker coroutine,
meaning that also it won't crash the parent job.

**Author**
Matti Krause

