package kotlinx.coroutines

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin/RequiresOptIn.Level

@Retention(AnnotationRetention.BINARY)
@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY])
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD])
@RequiresOptIn(message = "This is an internal kotlinx.coroutines API that should not be used from outside of kotlinx.coroutines. No compatibility guarantees are provided. It is recommended to report your use-case of internal API to kotlinx.coroutines issue tracker, so stable API could be provided instead", level = kotlin.RequiresOptIn.Level.ERROR)
annotation class InternalCoroutinesApi(

)
