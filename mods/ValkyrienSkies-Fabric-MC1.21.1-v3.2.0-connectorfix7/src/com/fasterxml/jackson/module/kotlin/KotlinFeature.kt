package com.fasterxml.jackson.module.kotlin

import java.util.ArrayList
import java.util.BitSet

public enum class KotlinFeature(enabledByDefault: Boolean) {
   NullToEmptyCollection(false),
   NullToEmptyMap(false),
   NullIsSameAsDefault(false),
   SingletonSupport(false),
   StrictNullChecks(false);

   private final val enabledByDefault: Boolean
   internal final val bitSet: BitSet

   init {
      this.enabledByDefault = enabledByDefault
      this.bitSet = ExtensionsKt.toBitSet((int)Math.pow(2.0, (double)this.ordinal()))
   }

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<KotlinFeature> {
      arrayOf(NullToEmptyCollection, NullToEmptyMap, NullIsSameAsDefault, SingletonSupport, StrictNullChecks)
   }

   public companion object {
      internal final val defaults: BitSet
         internal final get() {
            val var1: BitSet = ExtensionsKt.toBitSet(0)
            val `$this$_get_defaults__u24lambda_u2d2`: BitSet = var1
            val `$this$filterTo$iv$iv`: Array<Any> = KotlinFeature.values()
            val `element$iv`: java.util.Collection = ArrayList()
            val var11: Array<Any> = `$this$filterTo$iv$iv`
            val var12: Int = `$this$filterTo$iv$iv`.length

            repeat(var12) { var13 ->
               val `element$iv$iv`: Any = var11[var13]
               if (((KotlinFeature)var11[var13]).enabledByDefault) {
                  `element$iv`.add(`element$iv$iv`)
               }
            }

            for (var20 in `element$iv` as java.util.List) {
               `$this$_get_defaults__u24lambda_u2d2`.or((var20 as KotlinFeature).bitSet)
            }

            return var1
         }

   }
}
