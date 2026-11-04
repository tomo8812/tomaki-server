package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nExceptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Exceptions.kt\nkotlinx/coroutines/JobCancellationException\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,76:1\n26#2:77\n*S KotlinDebug\n*F\n+ 1 Exceptions.kt\nkotlinx/coroutines/JobCancellationException\n*L\n44#1:77\n*E\n"])
internal class JobCancellationException(message: String, cause: Throwable?, job: Job) : CancellationException(message),
   CopyableThrowable<JobCancellationException> {
   @JvmField
   @NotNull
   internal final val job: Job

   init {
      this.job = job
      if (cause != null) {
         this.initCause(cause)
      }
   }

   public override fun fillInStackTrace(): Throwable {
      if (DEBUG) {
         return super.fillInStackTrace()
      } else {
         this.setStackTrace(arrayOfNulls(0))
         return this
      }
   }

   public open fun createCopy(): JobCancellationException? {
      if (DEBUG) {
         val var10002: java.lang.String = this.getMessage()
         return JobCancellationException(var10002, this, this.job)
      } else {
         return null
      }
   }

   public override fun toString(): String {
      return "${super.toString()}; job=${this.job}"
   }

   public override operator fun equals(other: Any?): Boolean {
      return other === this
         || other is JobCancellationException
            && (other as JobCancellationException).getMessage() == this.getMessage()
            && (other as JobCancellationException).job == this.job
            && (other as JobCancellationException).getCause() == this.getCause()
         }

   public override fun hashCode(): Int {
      val var10000: java.lang.String = this.getMessage()
      val var1: Int = (var10000.hashCode() * 31 + this.job.hashCode()) * 31
      val var10001: java.lang.Throwable = this.getCause()
      return var1 + (if (var10001 != null) var10001.hashCode() else 0)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun createCopy(): java.lang.Throwable {
      this.createCopy() as java.lang.Throwable
   }
}
