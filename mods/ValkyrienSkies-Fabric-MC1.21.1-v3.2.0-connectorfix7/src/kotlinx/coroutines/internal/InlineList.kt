package kotlinx.coroutines.internal

import java.util.ArrayList
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.SourceDebugExtension

@JvmInline
@SourceDebugExtension(["SMAP\nInlineList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InlineList.kt\nkotlinx/coroutines/internal/InlineList\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1#2:50\n*E\n"])
internal value class InlineList<E> {
   private final val holder: Any?

   @JvmStatic
   public operator fun plus(element: Any): InlineList<Any> {
      if (ASSERTIONS_ENABLED && element is java.util.List) {
         throw AssertionError()
      } else {
         val var10000: Any
         if (arg0 == null) {
            var10000 = constructor_impl/* $VF was: constructor-impl */(element)
         } else if (arg0 is ArrayList) {
            (arg0 as ArrayList).add(element)
            var10000 = constructor_impl/* $VF was: constructor-impl */(arg0)
         } else {
            val list: ArrayList = ArrayList(4)
            list.add(arg0)
            list.add(element)
            var10000 = constructor_impl/* $VF was: constructor-impl */(list)
         }

         return var10000
      }
   }

   @JvmStatic
   public inline fun forEachReversed(action: (Any) -> Unit) {
      if (arg0 != null) {
         if (arg0 !is ArrayList) {
            action(arg0)
         } else {
            val list: ArrayList = arg0 as ArrayList

            for (i in (arg0 as ArrayList).size() - 1 downTo 0) {
               action(list.get(i))
            }
         }
      }
   }

   @JvmStatic
   public open fun toString(): String {
      return "InlineList(holder=$arg0)"
   }

   override fun toString(): java.lang.String {
      toString_impl/* $VF was: toString-impl */(this.holder)
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return if (arg0 == null) 0 else arg0.hashCode()
   }

   override fun hashCode(): Int {
      hashCode_impl/* $VF was: hashCode-impl */(this.holder)
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      return other is InlineList && arg0 == (other as InlineList).unbox_impl/* $VF was: unbox-impl */()
   }

   override fun equals(other: Any): Boolean {
      equals_impl/* $VF was: equals-impl */(this.holder, other)
   }

   @JvmSynthetic
   fun InlineList(holder: Any) {
      this.holder = holder
   }

   @JvmStatic
   fun <E> `constructor-impl`(holder: Any?) {
      holder
   }

   @JvmStatic
   @JvmSynthetic
   fun `constructor-impl$default`(var0: Any, var1: Int, var2: DefaultConstructorMarker) {
      if ((var1 and 1) != 0) {
         var0 = null
      }

      constructor_impl/* $VF was: constructor-impl */(var0)
   }

   @JvmStatic
   @JvmSynthetic
   fun `box-impl`(v: Any): InlineList {
      InlineList(v)
   }

   @JvmSynthetic
   fun `unbox-impl`() {
      this.holder
   }

   @JvmStatic
   fun `equals-impl0`(p1: Any, p2: Any): Boolean {
      p1 == p2
   }
}
