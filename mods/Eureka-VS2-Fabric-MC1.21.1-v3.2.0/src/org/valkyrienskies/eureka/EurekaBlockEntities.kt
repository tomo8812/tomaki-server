package org.valkyrienskies.eureka

import com.mojang.datafixers.types.Type
import java.util.ArrayList
import java.util.Arrays
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1208
import net.minecraft.class_156
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2586
import net.minecraft.class_2591
import net.minecraft.class_2680
import net.minecraft.class_5321
import net.minecraft.class_7924
import net.minecraft.class_2591.class_2592
import net.minecraft.class_2591.class_5559
import org.valkyrienskies.eureka.blockentity.EngineBlockEntity
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.eureka.registry.DeferredRegister
import org.valkyrienskies.eureka.registry.RegistrySupplier

@SourceDebugExtension(["SMAP\nEurekaBlockEntities.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaBlockEntities.kt\norg/valkyrienskies/eureka/EurekaBlockEntities\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,56:1\n1557#2:57\n1628#2,3:58\n37#3:61\n36#3,3:62\n*S KotlinDebug\n*F\n+ 1 EurekaBlockEntities.kt\norg/valkyrienskies/eureka/EurekaBlockEntities\n*L\n52#1:57\n52#1:58,3\n52#1:61\n52#1:62,3\n*E\n"])
public object EurekaBlockEntities {
   private final val BLOCKENTITIES: DeferredRegister<class_2591<*>>
   public final val SHIP_HELM: RegistrySupplier<class_2591<ShipHelmBlockEntity>> =
      SetsKt.setOf(
         arrayOf(
            EurekaBlocks.INSTANCE.OAK_SHIP_HELM,
            EurekaBlocks.INSTANCE.SPRUCE_SHIP_HELM,
            EurekaBlocks.INSTANCE.BIRCH_SHIP_HELM,
            EurekaBlocks.INSTANCE.JUNGLE_SHIP_HELM,
            EurekaBlocks.INSTANCE.ACACIA_SHIP_HELM,
            EurekaBlocks.INSTANCE.DARK_OAK_SHIP_HELM,
            EurekaBlocks.INSTANCE.CRIMSON_SHIP_HELM,
            EurekaBlocks.INSTANCE.WARPED_SHIP_HELM
         )
      ) withBE <unrepresentable>.INSTANCE as Function2 byName "ship_helm"
      public final val ENGINE: RegistrySupplier<class_2591<EngineBlockEntity>> =
      EurekaBlocks.INSTANCE.ENGINE withBE <unrepresentable>.INSTANCE as Function2 byName "engine"

   public fun register() {
      BLOCKENTITIES.applyAll()
   }

   private infix fun <T : class_2586> Set<RegistrySupplier<out class_2248>>.withBE(blockEntity: (class_2338, class_2680) -> Any): Pair<
         Set<RegistrySupplier<out class_2248>>,
         (class_2338, class_2680) -> Any
      > {
      return Pair(`$this$withBE`, blockEntity)
   }

   private infix fun <T : class_2586> RegistrySupplier<out class_2248>.withBE(blockEntity: (class_2338, class_2680) -> Any): Pair<
         Set<RegistrySupplier<out class_2248>>,
         (class_2338, class_2680) -> Any
      > {
      return Pair(SetsKt.setOf(`$this$withBE`), blockEntity)
   }

   private infix fun <T : class_2586> class_2248.withBE(blockEntity: (class_2338, class_2680) -> Any): Pair<class_2248, (class_2338, class_2680) -> Any> {
      return Pair(`$this$withBE`, blockEntity)
   }

   private infix fun <T : class_2586> Pair<Set<RegistrySupplier<out class_2248>>, (class_2338, class_2680) -> Any>.byName(name: String): RegistrySupplier<
         class_2591<Any>
      > {
      return BLOCKENTITIES.register(name, { 
         val type: Type = class_156.method_29187(class_1208.field_5727, `$name`)
         val var10000: class_5559 = { blockPos: class_2338, blockState: class_2680 ->
            `$tmp0`(blockPos, blockState) as class_2586
         }
         val `$this$toTypedArray$iv`: java.lang.Iterable = `$this_byName`.getFirst() as java.lang.Iterable
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$toTypedArray$iv`, 10))

         for (`item$iv$iv` in `$this$toTypedArray$iv`) {
            `destination$iv$iv`.add((`item$iv$iv` as RegistrySupplier).get() as class_2248)
         }

         val var3: Array<class_2248> = (`destination$iv$iv` as java.util.List).toArray(arrayOfNulls(0))
         val var18: class_2591 = class_2592.method_20528(var10000, Arrays.copyOf(var3, var3.length)).method_11034(type)
         var18
      })
   }

   @JvmStatic
   fun {
      val var10000: DeferredRegister.Companion = DeferredRegister.Companion
      val var10002: class_5321 = class_7924.field_41255
      BLOCKENTITIES = var10000.create("vs_eureka", var10002)
   }
}
