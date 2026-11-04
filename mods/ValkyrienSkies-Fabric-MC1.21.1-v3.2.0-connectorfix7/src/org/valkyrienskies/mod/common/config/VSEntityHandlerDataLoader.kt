package org.valkyrienskies.mod.common.config

import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KProperty
import net.minecraft.class_1299
import net.minecraft.class_2960
import net.minecraft.class_3300
import net.minecraft.class_3695
import net.minecraft.class_4309
import net.minecraft.class_7923
import org.apache.logging.log4j.Logger
import org.valkyrienskies.mod.common.entity.handling.VSEntityHandler
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

@SourceDebugExtension(["SMAP\nVSEntityHandlerDataLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSEntityHandlerDataLoader.kt\norg/valkyrienskies/mod/common/config/VSEntityHandlerDataLoader\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,36:1\n216#2,2:37\n*S KotlinDebug\n*F\n+ 1 VSEntityHandlerDataLoader.kt\norg/valkyrienskies/mod/common/config/VSEntityHandlerDataLoader\n*L\n21#1:37,2\n*E\n"])
public object VSEntityHandlerDataLoader : class_4309(Gson(), "vs_entities") {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(
         PropertyReference1Impl(VSEntityHandlerDataLoader.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
      )
   );

   private final val logger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(logger$delegate, this, $$delegatedProperties[0])
      }


   protected open fun apply(list: MutableMap<class_2960, JsonElement>, resourceManager: class_3300, profiler: class_3695) {
      for (`element$iv` in list.entrySet()) {
         val l: class_2960 = `element$iv`.getKey() as class_2960
         val v: JsonElement = `element$iv`.getValue() as JsonElement

         try {
            val var10000: class_1299 = class_7923.field_41177.method_17966(l).orElse(null) as class_1299
            if (var10000 != null) {
               val var15: VSEntityManager = VSEntityManager.INSTANCE
               val var10001: class_2960 = class_2960.method_60654(v.getAsJsonObject().get("handler").getAsString())
               val var16: VSEntityHandler = var15.getHandler(var10001)
               if (var16 == null) {
                  throw Exception("Handler not found")
               }

               VSEntityManager.INSTANCE.pair(var10000, var16)
            }
         } catch (var14: Exception) {
            INSTANCE.logger.error("Error loading entity handler data for entity type $l", var14)
         }
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_18788(`object`: Any, resourceManager: class_3300, profilerFiller: class_3695) {
      this.apply(`object` as MutableMap<class_2960, JsonElement>, resourceManager, profilerFiller)
   }
}
