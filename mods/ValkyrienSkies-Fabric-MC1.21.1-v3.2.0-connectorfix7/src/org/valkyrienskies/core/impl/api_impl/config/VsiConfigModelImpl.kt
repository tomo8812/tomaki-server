package org.valkyrienskies.core.impl.api_impl.config

import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.full.KClasses
import kotlin.reflect.jvm.ReflectJvmMapping
import org.valkyrienskies.core.internal.config.ConfigCategory
import org.valkyrienskies.core.internal.config.ConfigEntry
import org.valkyrienskies.core.internal.config.VsiConfigModel
import org.valkyrienskies.core.internal.config.VsiConfigModelCategory

public data class VsiConfigModelImpl(root: VsiConfigModelCategory) : VsiConfigModel {
   public open val root: VsiConfigModelCategory

   init {
      this.root = root
   }

   public operator fun component1(): VsiConfigModelCategory {
      return this.root
   }

   public fun copy(root: VsiConfigModelCategory = this.root): VsiConfigModelImpl {
      return VsiConfigModelImpl(root)
   }

   public override fun toString(): String {
      return "VsiConfigModelImpl(root=${this.root})"
   }

   public override fun hashCode(): Int {
      return this.root.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is VsiConfigModelImpl && this.root == (other as VsiConfigModelImpl).root
      }
   }

   public companion object {
      public fun build(root: Any): VsiConfigModelImpl {
         return VsiConfigModelImpl(this.buildConfigTree(root, "root"))
      }

      private fun buildConfigTree(obj: Any, title: String): VsiConfigModelCategoryImpl {
         val root: VsiConfigModelCategoryImpl = VsiConfigModelCategoryImpl(title, null, 2, null)

         for (member in KClasses.getMemberProperties(obj.getClass()::class)) {
            val var10000: Field = ReflectJvmMapping.getJavaField(member as KProperty)
            if (var10000 != null) {
               var10000.setAccessible(true)
               val category: VsiConfigModelCategoryImpl = this.getCategory(var10000, obj)
               if (category != null) {
                  root.addCategory(category)
               } else {
                  val name: java.lang.String = member.getName()
                  val entry: VsiConfigModelEntryImpl = this.getEntry(var10000, name, obj)
                  if (entry != null) {
                     root.addEntry(name, entry)
                  }
               }
            }
         }

         return root
      }

      private fun getEntry(field: Field, name: String, obj: Any): VsiConfigModelEntryImpl<Any>? {
         val var10000: ConfigEntry = field.getAnnotation(ConfigEntry.class)
         return if (var10000 != null) VsiConfigModelEntryImpl.Companion.build({
            val var10000: Any = field.get(obj)
            return var10000
         } as () -> Any, { v: Any ->
            field.set(obj, v)
         } as (Any?) -> Unit, var10000.min, var10000.max, name, var10000.description) else null
      }

      private fun getCategory(field: Field, obj: Any): VsiConfigModelCategoryImpl? {
         val var10000: ConfigCategory = field.getAnnotation(ConfigCategory.class)
         val var6: VsiConfigModelCategoryImpl
         if (var10000 != null) {
            val var5: VsiConfigModelImpl.Companion = VsiConfigModelImpl.Companion
            val var10001: Any = field.get(obj)
            var6 = var5.buildConfigTree(var10001, var10000.title)
         } else {
            var6 = null
         }

         return var6
      }
   }
}
