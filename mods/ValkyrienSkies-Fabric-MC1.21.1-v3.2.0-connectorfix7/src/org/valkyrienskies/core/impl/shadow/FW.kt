package org.valkyrienskies.core.impl.shadow

import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.RunnableFuture
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger

public class FW(p0: Int, p1: Int, p2: Int = 5, p3: Long = 60L, p4: TimeUnit = TimeUnit.SECONDS, p5: PriorityBlockingQueue<Runnable> = PriorityBlockingQueue()) : ThreadPoolExecutor(
      var1, var2, var4, var6, var7, var3.org/valkyrienskies/core/impl/shadow/FW##Lambda_0_72(var3)
   ) {
   @JvmStatic
   protected open fun <T> a(p0: Runnable, p1: Any): org.valkyrienskies.core.impl.shadow.FW.a.a<Any> {
      throw NotImplementedError("An operation is not implemented: Not implemented");
   }

   @JvmStatic
   protected open fun <T> a(p0: Callable<Any>): org.valkyrienskies.core.impl.shadow.FW.a.a<Any> {
      return FW.a.a(var0);
   }

   public fun <T> a(p0: Long, p1: Callable<Any>): org.valkyrienskies.core.impl.shadow.FW.a.a<Any> {
      val var10000: Future = this.submit(FW.a.b(var1, var3));
      return var10000 as FW.a.a<T>;
   }

   @JvmStatic
   public fun <T> a(p0: org.valkyrienskies.core.impl.shadow.FW.a.a<Any>, p1: Long): org.valkyrienskies.core.impl.shadow.FW.a.a<Any>? {
      throw NotImplementedError(null, 1, null);
   }

   @JvmStatic
   fun a(var0: Int, var1: Runnable): Thread {
      val var2: Thread;
      (var2 = Thread(var1)).setPriority(var0);
      var2;
   }

   @JvmStatic
   fun a(var0: Runnable, var1: Any) {
      var0.run();
      var1;
   }

   @JvmSynthetic
   override final fun newTaskFor(p0: Runnable, p1: Any): RunnableFuture {
      throw NotImplementedError("An operation is not implemented: Not implemented");
   }

   @JvmSynthetic
   override final fun newTaskFor(p0: Callable): RunnableFuture {
      val var2: Callable;
      FW.a.a(var2) as RunnableFuture;
   }

   public companion object a {
      @JvmStatic
      @JvmSynthetic
      KProperty<Object>[] a;

      private final val a: Logger
         private final get() {
            val var10000: Logger = FW.b;
            return var10000;
         }


      @JvmStatic
      @JvmSynthetic
      fun a(var0: FW.a): Logger {
         val var10000: Logger = FW.b;
         var10000;
      }

      @JvmStatic
      fun {
         val var0: Array<KProperty>;
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(FW.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         );
         a = var0;
      }

      // $VF: Class flags could not be determined
      internal class a<T> : FutureTask<T>, java.lang.Comparable<FW.a.a<T>> {
         private FW.a.b<T> a;

         fun a(var1: Callable<T>) {
            super(var1);
            this.a = var1 as FW.a.b<T>;
         }

         fun a(): FW.a.b<T> {
            this.a;
         }

         fun a(var1: FW.a.a<T>): Int {
            if (this == var1) {
               0;
            } else if (var1 == null) {
               1;
            } else {
               var var2: FW.a.b = this.a;
               var2 = var1.a;
               if (this.a.a == var1.a.a) {
                  if (this.hashCode() != var1.hashCode()) {
                     Intrinsics.compare(this.hashCode(), var1.hashCode());
                  } else {
                     FW.a.a(FW.a).warn("Ruby says \"this should work just fine\", lets hope it does \ud83d\ude02");
                     1;
                  }
               } else {
                  var2 = this.a;
                  var2 = var1.a;
                  Intrinsics.compare(this.a.a, var1.a.a);
               }
            }
         }

         @JvmSynthetic
         override final fun compareTo(other: Any): Int {
            val var2: FW.a.a = other as FW.a.a;
            if (this == other as FW.a.a) {
               0;
            } else if (var2 == null) {
               1;
            } else {
               var var3: FW.a.b = this.a;
               var3 = var2.a;
               val var10000: Int;
               if (this.a.a == var2.a.a) {
                  if (this.hashCode() == var2.hashCode()) {
                     FW.a.a(FW.a).warn("Ruby says \"this should work just fine\", lets hope it does \ud83d\ude02");
                     1;
                  }

                  var10000 = Intrinsics.compare(this.hashCode(), var2.hashCode());
               } else {
                  var3 = this.a;
                  var3 = var2.a;
                  var10000 = Intrinsics.compare(this.a.a, var2.a.a);
               }

               var10000;
            }
         }
      }

      // $VF: Class flags could not be determined
      internal class b<T> : Callable<T> {
         long a;
         private Callable<T> b;

         fun b(var1: Long, var3: Callable<T>) {
            this.a = var1;
            this.b = var3;
         }

         fun a(): Long {
            this.a;
         }

         fun b(): Callable<T> {
            this.b;
         }

         override final fun call(): T {
            try {
               this.b.call();
            } catch (var2: InterruptedException) {
               Thread.currentThread().interrupt();
               throw RuntimeException(var2);
            }
         }
      }
   }
}
