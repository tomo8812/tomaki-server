package org.valkyrienskies.core.impl.api_impl.config

import kotlin.jvm.internal.SourceDebugExtension
import org.valkyrienskies.core.internal.config.VsiConfigModelEntry

public data class VsiConfigModelEntryImpl<T>(getValue: () -> Any, setValue: (Any) -> Unit, min: Any?, max: Any?, name: String, description: String?) :
   VsiConfigModelEntry<T> {
   public open val getValue: () -> Any
   public open val setValue: (Any) -> Unit
   public open val min: Any?
   public open val max: Any?
   public open val name: String
   public open val description: String?
   public open val default: Any

   init {
      this.getValue = getValue
      this.setValue = setValue
      this.min = (T)min
      this.max = (T)max
      this.name = name
      this.description = description
      this.default = (T)this.getValue()
   }

   public operator fun component1(): () -> Any {
      return this.getValue
   }

   public operator fun component2(): (Any) -> Unit {
      return this.setValue
   }

   public operator fun component3(): Any? {
      return this.min
   }

   public operator fun component4(): Any? {
      return this.max
   }

   public operator fun component5(): String {
      return this.name
   }

   public operator fun component6(): String? {
      return this.description
   }

   public fun copy(
      getValue: () -> Any = this.getValue,
      setValue: (Any) -> Unit = this.setValue,
      min: Any? = this.min,
      max: Any? = this.max,
      name: String = this.name,
      description: String? = this.description
   ): VsiConfigModelEntryImpl<Any> {
      return VsiConfigModelEntryImpl<>(getValue, setValue, (T)min, (T)max, name, description)
   }

   public override fun toString(): String {
      return "VsiConfigModelEntryImpl(getValue=${this.getValue}, setValue=${this.setValue}, min=${this.min}, max=${this.max}, name=${this.name}, description=${this.description})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        ((this.getValue.hashCode() * 31 + this.setValue.hashCode()) * 31 + (if (this.min == null) 0 else this.min.hashCode())) * 31
                           + (if (this.max == null) 0 else this.max.hashCode())
                     )
                     * 31
                  + this.name.hashCode()
            )
            * 31
         + (if (this.description == null) 0 else this.description.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label52@
      if (this === other) {
         return true
      } else {
         return other is VsiConfigModelEntryImpl
            && this.getValue == (other as VsiConfigModelEntryImpl).getValue
            && this.setValue == (other as VsiConfigModelEntryImpl).setValue
            && this.min == (other as VsiConfigModelEntryImpl).min
            && this.max == (other as VsiConfigModelEntryImpl).max
            && this.name == (other as VsiConfigModelEntryImpl).name
            && this.description == (other as VsiConfigModelEntryImpl).description
         }
   }

   @SourceDebugExtension(["SMAP\nVsiConfigModelEntryImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VsiConfigModelEntryImpl.kt\norg/valkyrienskies/core/impl/api_impl/config/VsiConfigModelEntryImpl$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1#2:50\n*E\n"])
   public companion object {
      public fun <T> build(getValue: () -> Any, setValue: (Any) -> Unit, min: Double, max: Double, name: String, description: String): VsiConfigModelEntryImpl<
            Any
         > {
         return VsiConfigModelEntryImpl<>(
            getValue,
            setValue,
            this.processBoundingValue((T)getValue(), min),
            this.processBoundingValue((T)getValue(), max),
            name,
            if (description.length() > 0) description else null
         )
      }

      private fun <T> processBoundingValue(value: Any, bound: Double): Any? {
         return (T)(if (java.lang.Double.isNaN(bound))
            null
            else
            (
               if (value is Int)
                  (int)bound
                  else
                  (if (value is java.lang.Double) bound else (if (value is java.lang.Float) (float)bound else ((long)bound as? Any)))
            ))
      }
   }
}
