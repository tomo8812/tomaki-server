package org.valkyrienskies.eureka

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1299
import net.minecraft.class_1311
import net.minecraft.class_1937
import net.minecraft.class_5321
import net.minecraft.class_5617
import net.minecraft.class_7924
import net.minecraft.class_1299.class_1300
import org.valkyrienskies.eureka.registry.DeferredRegister
import org.valkyrienskies.eureka.registry.RegistrySupplier

@SourceDebugExtension(["SMAP\nEurekaEntities.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaEntities.kt\norg/valkyrienskies/eureka/EurekaEntities\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,50:1\n1#2:51\n1863#3,2:52\n*S KotlinDebug\n*F\n+ 1 EurekaEntities.kt\norg/valkyrienskies/eureka/EurekaEntities\n*L\n48#1:52,2\n*E\n"])
public object EurekaEntities {
   private final val ENTITIES: DeferredRegister<class_1299<*>>
   private final val ENTITY_RENDERERS: MutableList<ToRegEntityRenderer<*>> = ArrayList() as java.util.List

   public fun register() {
      ENTITIES.applyAll()
   }

   private infix fun <T : class_1297> ((class_1299<Any>, class_1937) -> Any).category(category: class_1311): class_1300<Any> {
      return class_1300.method_5903({ entityType: class_1299, level: class_1937 ->
         `$tmp0`(entityType, level) as class_1297
      }, category)
   }

   private infix fun <T : class_1297> class_1300<Any>.configure(run: (class_1300<Any>) -> Unit): class_1300<Any> {
      run(`$this$configure`)
      return `$this$configure`
   }

   private infix fun <T : class_1297> RegistrySupplier<class_1299<Any>>.registerRenderer(factory: class_5617<in Any>): RegistrySupplier<class_1299<Any>> {
      ENTITY_RENDERERS.add(ToRegEntityRenderer(`$this$registerRenderer`, factory))
      return `$this$registerRenderer`
   }

   private infix fun <T : class_1297> class_1300<Any>.byName(name: String): RegistrySupplier<class_1299<Any>> {
      return ENTITIES.register(name, { 
         `$this_byName`.method_5905(`$name`)
      })
   }

   @JvmStatic
   public fun registerRenderers() {
      for (`element$iv` in ENTITY_RENDERERS) {
         (`element$iv` as ToRegEntityRenderer).register()
      }
   }

   @JvmStatic
   fun {
      val var10000: DeferredRegister.Companion = DeferredRegister.Companion
      val var10002: class_5321 = class_7924.field_41266
      ENTITIES = var10000.create("vs_eureka", var10002)
   }
}
