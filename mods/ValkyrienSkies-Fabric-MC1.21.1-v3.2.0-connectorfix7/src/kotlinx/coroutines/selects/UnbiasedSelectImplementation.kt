package kotlinx.coroutines.selects

import java.util.ArrayList
import java.util.Collections
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.selects.SelectImplementation.ClauseData

@PublishedApi
@SourceDebugExtension(["SMAP\nSelectUnbiased.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SelectUnbiased.kt\nkotlinx/coroutines/selects/UnbiasedSelectImplementation\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,68:1\n1855#2,2:69\n*S KotlinDebug\n*F\n+ 1 SelectUnbiased.kt\nkotlinx/coroutines/selects/UnbiasedSelectImplementation\n*L\n63#1:69,2\n*E\n"])
internal open class UnbiasedSelectImplementation<R>(context: CoroutineContext) : SelectImplementation(context) {
   private final val clausesToRegister: MutableList<ClauseData> = ArrayList() as java.util.List

   public override operator fun SelectClause0.invoke(block: (Continuation<Any>) -> Any?) {
      this.clausesToRegister
         .add(
            this as SelectImplementation.ClauseData(
               `$this$invoke`.getClauseObject(),
               `$this$invoke`.getRegFunc(),
               `$this$invoke`.getProcessResFunc(),
               PARAM_CLAUSE_0,
               block,
               `$this$invoke`.getOnCancellationConstructor()
            )
         )
      }

   public override operator fun <Q> SelectClause1<Any>.invoke(block: (Any, Continuation<Any>) -> Any?) {
      this.clausesToRegister
         .add(
            this as SelectImplementation.ClauseData(
               `$this$invoke`.getClauseObject(),
               `$this$invoke`.getRegFunc(),
               `$this$invoke`.getProcessResFunc(),
               null,
               block,
               `$this$invoke`.getOnCancellationConstructor()
            )
         )
      }

   public override operator fun <P, Q> SelectClause2<Any, Any>.invoke(param: Any, block: (Any, Continuation<Any>) -> Any?) {
      this.clausesToRegister
         .add(
            this as SelectImplementation.ClauseData(
               `$this$invoke`.getClauseObject(),
               `$this$invoke`.getRegFunc(),
               `$this$invoke`.getProcessResFunc(),
               param,
               block,
               `$this$invoke`.getOnCancellationConstructor()
            )
         )
      }

   @PublishedApi
   internal override suspend fun doSelect(): Any {
      return doSelect$suspendImpl(this, `$completion`)
   }

   @PublishedApi
   @JvmStatic
   @JvmSynthetic
   fun <R> `doSelect$suspendImpl`(`$this`: UnbiasedSelectImplementation<R>, `$completion`: Continuation<in R>) {
      `$this`.shuffleAndRegisterClauses()
      `$this`.doSelect(`$completion`)
   }

   private fun shuffleAndRegisterClauses() {
      try {
         Collections.shuffle(this.clausesToRegister)

         for (`element$iv` in this.clausesToRegister) {
            SelectImplementation.register$default(this, `element$iv` as SelectImplementation.ClauseData, false, 1, null)
         }
      } finally {
         this.clausesToRegister.clear()
      }
   }
}
