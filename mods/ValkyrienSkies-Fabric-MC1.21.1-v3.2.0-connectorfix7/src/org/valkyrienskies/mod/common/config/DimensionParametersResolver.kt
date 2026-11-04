package org.valkyrienskies.mod.common.config

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import java.util.HashMap
import java.util.NoSuchElementException
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2960
import net.minecraft.class_3300
import net.minecraft.class_3695
import net.minecraft.class_4309
import org.apache.logging.log4j.Logger
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.mod.util.LoggingKt

@SourceDebugExtension(["SMAP\nDimensionParametersResolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DimensionParametersResolver.kt\norg/valkyrienskies/mod/common/config/DimensionParametersResolver\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,75:1\n216#2:76\n217#2:79\n1863#3,2:77\n381#4,7:80\n*S KotlinDebug\n*F\n+ 1 DimensionParametersResolver.kt\norg/valkyrienskies/mod/common/config/DimensionParametersResolver\n*L\n27#1:76\n27#1:79\n31#1:77,2\n58#1:80,7\n*E\n"])
public object DimensionParametersResolver : class_4309(Gson(), "vs_dimension_parameters") {
   public final val logger: Logger = LoggingKt.logger("(Valkyrien Skies) Bloon Factory")
   public final var dimensionMap: Map<String, org.valkyrienskies.mod.common.config.DimensionParametersResolver.Parameters> = HashMap() as java.util.Map

   protected open fun apply(objects: Map<class_2960?, JsonElement?>, resourceManager: class_3300, profiler: class_3695) {
      val temp: HashMap = HashMap()

      for (`element$iv` in objects.entrySet()) {
         val key: class_2960 = `element$iv`.getKey() as class_2960
         val value: JsonElement = `element$iv`.getValue() as JsonElement
         if (key != null && value != null) {
            try {
               if (value.isJsonArray()) {
                  val var10000: JsonArray = value.getAsJsonArray()

                  for (`element$ivx` in var10000 as java.lang.Iterable) {
                     val it: JsonElement = `element$ivx` as JsonElement
                     val var20: DimensionParametersResolver = INSTANCE
                     var20.parse(it, temp)
                  }
               } else {
                  if (!value.isJsonObject()) {
                     throw IllegalArgumentException()
                  }

                  INSTANCE.parse(value, temp)
               }
            } catch (var19: Exception) {
               logger.error(ExceptionsKt.stackTraceToString(var19))
            }
         }
      }

      dimensionMap = temp
   }

   private fun parse(element: JsonElement, map: MutableMap<String, org.valkyrienskies.mod.common.config.DimensionParametersResolver.Parameters>) {
      var var10000: JsonElement = element.getAsJsonObject().get("maxYPos")
      if (var10000 != null) {
         val maxYPos: Double = var10000.getAsDouble()
         var10000 = element.getAsJsonObject().get("seaLevel")
         if (var10000 != null) {
            val seaLevel: Double = var10000.getAsDouble()
            var10000 = element.getAsJsonObject().get("gravity")
            if (var10000 != null) {
               val var20: JsonArray = var10000.getAsJsonArray()
               if (var20 != null) {
                  var10000 = element.getAsJsonObject().get("dimensionId")
                  if (var10000 != null) {
                     val var22: java.lang.String = var10000.getAsString()
                     if (var22 != null) {
                        var10000 = element.getAsJsonObject().get("priority")
                        val priority: Int = if (var10000 != null) var10000.getAsInt() else 0
                        val var12: Any = map.get(var22)
                        val var24: Any
                        if (var12 == null) {
                           val `answer$iv`: Any = DimensionParametersResolver.Parameters(maxYPos, seaLevel, parse$toVector3d(var20), priority)
                           map.put(var22, `answer$iv`)
                           var24 = `answer$iv`
                        } else {
                           var24 = var12
                        }

                        if ((var24 as DimensionParametersResolver.Parameters).priority < priority) {
                           map.put(var22, DimensionParametersResolver.Parameters(maxYPos, seaLevel, parse$toVector3d(var20), priority))
                        }

                        return
                     }
                  }

                  throw NoSuchElementException("Parameter \"dimensionId\" wasn't filled")
               }
            }

            throw NoSuchElementException("Parameter \"gravity\" wasn't filled")
         } else {
            throw NoSuchElementException("Parameter \"seaLevel\" wasn't filled")
         }
      } else {
         throw NoSuchElementException("Parameter \"maxYPos\" wasn't filled")
      }
   }

   @JvmStatic
   fun JsonArray.`parse$toVector3d`(): Vector3dc {
      Vector3d(
         `$this$parse_u24toVector3d`.get(0).getAsDouble(), `$this$parse_u24toVector3d`.get(1).getAsDouble(), `$this$parse_u24toVector3d`.get(2).getAsDouble()
      ) as Vector3dc
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_18788(`object`: Any, resourceManager: class_3300, profilerFiller: class_3695) {
      this.apply(`object` as MutableMap<class_2960, JsonElement>, resourceManager, profilerFiller)
   }

   public data class Parameters(maxY: Double, seaLevel: Double, gravity: Vector3dc = DEFAULT_WORLD_GRAVITY, priority: Int = -1) {
      public final val maxY: Double
      public final val seaLevel: Double
      public final var gravity: Vector3dc
      public final val priority: Int

      init {
         this.maxY = maxY
         this.seaLevel = seaLevel
         this.gravity = gravity
         this.priority = priority
      }

      public operator fun component1(): Double {
         return this.maxY
      }

      public operator fun component2(): Double {
         return this.seaLevel
      }

      public operator fun component3(): Vector3dc {
         return this.gravity
      }

      public operator fun component4(): Int {
         return this.priority
      }

      public fun copy(maxY: Double = this.maxY, seaLevel: Double = this.seaLevel, gravity: Vector3dc = this.gravity, priority: Int = this.priority): org.valkyrienskies.mod.common.config.DimensionParametersResolver.Parameters {
         return DimensionParametersResolver.Parameters(maxY, seaLevel, gravity, priority)
      }

      public override fun toString(): String {
         return "Parameters(maxY=${this.maxY}, seaLevel=${this.seaLevel}, gravity=${this.gravity}, priority=${this.priority})"
      }

      public override fun hashCode(): Int {
         return ((java.lang.Double.hashCode(this.maxY) * 31 + java.lang.Double.hashCode(this.seaLevel)) * 31 + this.gravity.hashCode()) * 31
            + Integer.hashCode(this.priority)
         }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is DimensionParametersResolver.Parameters
               && java.lang.Double.compare(this.maxY, (other as DimensionParametersResolver.Parameters).maxY) == 0
               && java.lang.Double.compare(this.seaLevel, (other as DimensionParametersResolver.Parameters).seaLevel) == 0
               && this.gravity == (other as DimensionParametersResolver.Parameters).gravity
               && this.priority == (other as DimensionParametersResolver.Parameters).priority
            }
      }
   }
}
