package org.valkyrienskies.core.impl.shadow

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import java.util.ArrayList
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2

public class Gk<S>(p0: Any, p1: List<Gu<Any>>, p2: Boolean = false, p3: Boolean = true, p4: ((String) -> Unit)? = null) : Gi<S> {
   private final val f: Any
   private final val a: List<Gu<Any>>
   private final val d: Boolean
   private final val c: Boolean
   private final val e: (String) -> Unit
   private final val g: ArrayList<Any>
   private final val b: IntSet

   init {
      this.a = (S)var1
      this.b = var2
      this.c = var3
      this.d = var4
      var var10001: Function1 = var5
      if (var5 == null) {
         var10001 = <unrepresentable>.a
      }

      this.e = var10001
      this.f = ArrayList<>()
      this.g = IntOpenHashSet() as IntSet
   }

   public override fun a(p0: Any) {
      val var2: Boolean = var1 == this.a
      var var3: Int = this.f.isEmpty()
      if (!this.c || var2 || !var3) {
         if (!var2 && var3) {
            throw IllegalArgumentException("First executed stage must be the reset (${this.a}) but was $var1")
         } else {
            val var4: java.util.List = ArrayList()
            if (var2 && !var3) {
               val var14: java.util.Iterator = SequencesKt.filterIndexed(CollectionsKt.asSequence(this.b), { p0: Int, p1: Gu<Any> ->
                  return !Gk.this.g.contains(var1)
               } as Function2).iterator()

               while (var14.hasNext()) {
                  val var10000: java.lang.String = (var14.next() as Gu).a(this.f, true)
                  if (var10000 != null) {
                     var4.add(var10000)
                  }
               }

               if (!var4.isEmpty()) {
                  this.e(
                     "Constraints failed (last stage before reset). Stages since last reset: ${this.f}\n${CollectionsKt.joinToString$default(
                        var4, "\n", null, null, 0, null, null, 62, null
                     )}"
                  )
               }

               this.g.clear()
               this.f.clear()
            }

            this.f.add((S)var1)
            val var12: java.lang.Iterable = this.b
            var3 = 0

            for (var5 in var12) {
               if ((var1 = var3++) < 0) {
                  CollectionsKt.throwIndexOverflow()
               }

               var var20: java.lang.String = (var5 as Gu).a(this.f, false)
               if (var20 != null) {
                  if (this.d) {
                     this.g.add(var1)
                  }

                  var20 = var20
               } else {
                  var20 = null
               }

               if (var20 != null) {
                  var4.add(var20)
               }
            }

            if (!var4.isEmpty()) {
               this.e(
                  "Constraints failed. Stages since last reset: ${this.f}\n${CollectionsKt.joinToString$default(var4, "\n", null, null, 0, null, null, 62, null)}"
               )
            }
         }
      }
   }
}
