package org.valkyrienskies.mod.util

import kotlin.reflect.KProperty
import net.minecraft.class_1297
import net.minecraft.class_2940
import net.minecraft.class_2945

public class EntityDataDelegate<T>(data: class_2940<Any>) {
   public final val data: class_2940<Any>

   init {
      this.data = data
   }

   public fun get(thisRef: class_1297?): Any? {
      if (thisRef != null) {
         val var10000: class_2945 = thisRef.method_5841()
         if (var10000 != null) {
            return (T)var10000.method_12789(this.data)
         }
      }

      return null
   }

   public fun set(thisRef: class_1297?, value: Any) {
      if (thisRef != null) {
         val var10000: class_2945 = thisRef.method_5841()
         if (var10000 != null) {
            var10000.method_12778(this.data, value)
         }
      }
   }

   public operator fun getValue(thisRef: class_1297?, property: KProperty<*>): Any? {
      if (thisRef != null) {
         val var10000: class_2945 = thisRef.method_5841()
         if (var10000 != null) {
            return (T)var10000.method_12789(this.data)
         }
      }

      return null
   }

   public operator fun setValue(thisRef: class_1297?, property: KProperty<*>, value: Any) {
      if (thisRef != null) {
         val var10000: class_2945 = thisRef.method_5841()
         if (var10000 != null) {
            var10000.method_12778(this.data, value)
         }
      }
   }
}
