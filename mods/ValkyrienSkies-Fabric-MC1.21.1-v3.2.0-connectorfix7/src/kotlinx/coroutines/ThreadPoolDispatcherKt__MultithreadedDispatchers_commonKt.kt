@file:JvmMultifileClass
@file:JvmName("ThreadPoolDispatcherKt")

package kotlinx.coroutines

@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
public fun newSingleThreadContext(name: String): ExecutorCoroutineDispatcher {
   return ThreadPoolDispatcherKt.newFixedThreadPoolContext(1, name)
}
