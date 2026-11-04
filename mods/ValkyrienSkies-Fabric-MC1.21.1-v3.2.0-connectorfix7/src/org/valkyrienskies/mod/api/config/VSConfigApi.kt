package org.valkyrienskies.mod.api.config

import kotlin.jvm.internal.SourceDebugExtension
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue
import net.neoforged.neoforge.common.ModConfigSpec.Builder
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue
import org.valkyrienskies.core.impl.api_impl.config.VsiConfigModelImpl
import org.valkyrienskies.core.internal.config.VsiConfigModel
import org.valkyrienskies.core.internal.config.VsiConfigModelCategory
import org.valkyrienskies.core.internal.config.VsiConfigModelEntry
import org.valkyrienskies.core.internal.config.VsiConfigNode
import org.valkyrienskies.mod.common.config.ConfigType
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.mod.common.hooks.VSGameEvents.ConfigUpdateEntry

@SourceDebugExtension(["SMAP\nVSConfigApi.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSConfigApi.kt\norg/valkyrienskies/mod/api/config/VSConfigApi\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,161:1\n1#2:162\n*E\n"])
public object VSConfigApi {
   @JvmStatic
   public fun buildVSConfigModel(annotatedConfigObject: Any): VsiConfigModelImpl {
      return VsiConfigModelImpl.Companion.build(annotatedConfigObject)
   }

   @JvmStatic
   public fun buildModConfigSpec(
      configCategory: VsiConfigModelCategory,
      builder: Builder,
      forgeConfigValueConsumer: (String, ConfigValue<*>) -> Unit = { a: java.lang.String, b: ConfigValue ->
            Unit.INSTANCE
         }
   ): Builder {
      for (node in configCategory.children.entrySet()) {
         val entry: VsiConfigNode = node.getValue() as VsiConfigNode
         if (entry is VsiConfigModelCategory) {
            builder.translation((entry as VsiConfigModelCategory).title)
            builder.push((entry as VsiConfigModelCategory).title)
            buildModConfigSpec(entry as VsiConfigModelCategory, builder, forgeConfigValueConsumer)
            builder.pop()
         } else if (entry is VsiConfigModelEntry) {
            forgeConfigValueConsumer((entry as VsiConfigModelEntry).name, INSTANCE.defineNode(builder, entry as VsiConfigModelEntry<*>))
         }
      }

      return builder
   }

   public fun VsiConfigModel.applyToModel(getByKey: (String) -> Any?, configType: ConfigType, updatedEntries: MutableSet<ConfigUpdateEntry>) {
      forEachEntry$default(this, `$this$applyToModel`.root, null, lambda_2@{ category: java.util.List, node: VsiConfigModelEntry ->
         var var10000: Any = `$getByKey`(CollectionsKt.joinToString$default(CollectionsKt.plus(category, node.name), ".", null, null, 0, null, null, 62, null))
         if (var10000 == null) {
            return@lambda_2 Unit.INSTANCE
         } else {
            val newValue: Any = var10000
            var10000 = node.default
            if (var10000 == null) {
               return@lambda_2 Unit.INSTANCE
            } else {
               if (var10000 is java.lang.Float && var10000 is java.lang.Double) {
                  var10000 = (float)(var10000 as java.lang.Number).doubleValue()
               } else if (var10000 is java.lang.Enum) {
                  if (var10000 is java.lang.String) {
                     var10000 = (var10000 as java.lang.Enum).getDeclaringClass()
                     val var11: Array<java.lang.Enum> = var10000.getEnumConstants() as Array<java.lang.Enum>
                     var var12: Int = 0
                     val var13: Int = var11.length

                     while (true) {
                        if (var12 >= var13) {
                           var10000 = null
                           break
                        }

                        val var14: java.lang.Enum = var11[var12]
                        if (var11[var12].name() == newValue) {
                           var10000 = var14
                           break
                        }

                        var12++
                     }
                  } else {
                     var10000 = var10000 as? java.lang.Enum
                  }
               } else {
                  var10000 = if ((var10000.getClass()::class).isInstance(var10000)) var10000 else null
               }

               if (var10000 == null) {
                  return@lambda_2 Unit.INSTANCE
               } else {
                  if (!(var10000 == node.getValue())) {
                     `$updatedEntries`.add(VSGameEvents.ConfigUpdateEntry(`$configType`, category, node.name))
                  }

                  node.setValue(var10000)
                  return@lambda_2 Unit.INSTANCE
               }
            }
         }
      }, 1, null)
   }

   private fun VsiConfigModelCategory.forEachEntry(category: List<String> = CollectionsKt.emptyList(), callback: (List<String>, VsiConfigModelEntry<*>) -> Unit) {
      for (node in `$this$forEachEntry`.children.entrySet()) {
         val value: VsiConfigNode = node.getValue() as VsiConfigNode
         if (value is VsiConfigModelEntry) {
            callback(category, value)
         } else if (value is VsiConfigModelCategory) {
            this.forEachEntry(value as VsiConfigModelCategory, CollectionsKt.plus(category, (value as VsiConfigModelCategory).title), callback)
         }
      }
   }

   private fun prettifyCamelCase(name: String): String {
      if (name.length() == 0) {
         return name
      } else {
         val out: StringBuilder = StringBuilder()
         out.append(Character.toUpperCase(name.charAt(0)))
         var i: Int = 1

         for (var4 in name.length()..i) {
            val c: Char = name.charAt(i)
            if (Character.isUpperCase(c) && !Character.isUpperCase(name.charAt(i - 1))) {
               out.append(' ')
            }

            out.append(c)
         }

         val var10000: java.lang.String = out.toString()
         return var10000
      }
   }

   private fun defineNode(builder: Builder, entry: VsiConfigModelEntry<*>): ConfigValue<*> {
      val var10000: java.lang.String = entry.description
      if (var10000 != null) {
         builder.comment(var10000)
      }

      builder.translation(this.prettifyCamelCase(entry.name))
      val v: Any = entry.getValue()
      val var6: ConfigValue
      if (v is Int) {
         var6 = defineNode$defineNumeric(entry, builder, v as java.lang.Comparable)
      } else if (v is java.lang.Long) {
         var6 = defineNode$defineNumeric(entry, builder, v as java.lang.Comparable)
      } else if (v is java.lang.Float) {
         var6 = defineNode$defineNumeric(entry, builder, v as java.lang.Comparable)
      } else if (v is java.lang.Double) {
         var6 = defineNode$defineNumeric(entry, builder, v as java.lang.Comparable)
      } else if (v is java.lang.Boolean) {
         val var7: BooleanValue = builder.define(entry.name, v as java.lang.Boolean)
         var6 = var7 as ConfigValue
      } else if (v is java.lang.String) {
         var6 = defineNode$define(builder, entry, v)
      } else {
         if (v !is java.lang.Enum) {
            throw IllegalArgumentException("invalid config type $v of class ${if (v != null) v.getClass() else null}")
         }

         var6 = defineNode$defineEnum(entry, builder, v as java.lang.Enum<*>) as ConfigValue
      }

      return var6
   }

   @JvmStatic
   fun <T> `defineNode$define`(`$builder`: Builder, `$entry`: VsiConfigModelEntry<*>, v: T): ConfigValue<T> {
      `$builder`.define(`$entry`.name, v)
   }

   @JvmStatic
   fun <T extends java.lang.Enum<T>> `defineNode$defineEnum`(`$entry`: VsiConfigModelEntry<*>, builder: Builder, value: java.lang.Enum<*>): EnumValue<*> {
      val var10001: java.lang.String = `$entry`.name
      val var10000: EnumValue = builder.defineEnum(var10001, value)
      var10000
   }

   @JvmStatic
   fun <T extends java.lang.Comparable<? super T>> `defineNode$defineNumeric`(`$entry`: VsiConfigModelEntry<*>, `$builder`: Builder, v: T): ConfigValue<? extends java.lang.Comparable<?>> {
      val var10000: ConfigValue
      if (`$entry`.min != null && `$entry`.max != null) {
         if (v is Int) {
            val var10001: java.lang.String = `$entry`.name
            val var10002: Int = (v as java.lang.Number).intValue()
            val var10003: Any = `$entry`.min
            val var10: Int = var10003 as Int
            val var10004: Any = `$entry`.max
            var10000 = `$builder`.defineInRange(var10001, var10002, var10, var10004 as Int) as ConfigValue
         } else if (v is java.lang.Long) {
            val var4: java.lang.String = `$entry`.name
            val var7: Long = (v as java.lang.Number).longValue()
            val var11: Any = `$entry`.min
            val var12: Long = var11 as java.lang.Long
            val var17: Any = `$entry`.max
            var10000 = `$builder`.defineInRange(var4, var7, var12, var17 as java.lang.Long) as ConfigValue
         } else if (v is java.lang.Float) {
            val var5: java.lang.String = `$entry`.name
            val var8: Double = (v as java.lang.Number).floatValue()
            val var13: Any = `$entry`.min
            val var14: Double = (var13 as java.lang.Float).floatValue()
            val var18: Any = `$entry`.max
            var10000 = `$builder`.defineInRange(var5, var8, var14, (double)(var18 as java.lang.Float).floatValue()) as ConfigValue
         } else {
            if (v !is java.lang.Double) {
               throw IllegalArgumentException("Non numeric type $v not accepted")
            }

            val var6: java.lang.String = `$entry`.name
            val var9: Double = (v as java.lang.Number).doubleValue()
            val var15: Any = `$entry`.min
            val var16: Double = var15 as java.lang.Double
            val var19: Any = `$entry`.max
            var10000 = `$builder`.defineInRange(var6, var9, var16, var19 as java.lang.Double) as ConfigValue
         }
      } else {
         var10000 = defineNode$define(`$builder`, `$entry`, if (v is java.lang.Float) (double)(v as java.lang.Number).floatValue() else v)
      }

      var10000
   }
}
