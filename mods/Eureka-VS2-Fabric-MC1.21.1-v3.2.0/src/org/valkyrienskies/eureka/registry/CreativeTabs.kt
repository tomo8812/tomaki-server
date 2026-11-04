package org.valkyrienskies.eureka.registry

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1761
import net.minecraft.class_1799
import net.minecraft.class_1935
import net.minecraft.class_2561
import net.minecraft.class_1761.class_7704
import net.minecraft.class_1761.class_7915
import net.minecraft.class_1761.class_8128
import org.valkyrienskies.eureka.EurekaBlocks
import org.valkyrienskies.eureka.EurekaItems

@SourceDebugExtension(["SMAP\nCreativeTabs.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreativeTabs.kt\norg/valkyrienskies/eureka/registry/CreativeTabs\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,22:1\n1863#2,2:23\n*S KotlinDebug\n*F\n+ 1 CreativeTabs.kt\norg/valkyrienskies/eureka/registry/CreativeTabs\n*L\n15#1:23,2\n*E\n"])
public object CreativeTabs {
   public fun create(): class_1761 {
      val var10000: class_1761 = class_1761.method_47307(class_7915.field_41049, 0)
         .method_47321(class_2561.method_43471("itemGroup.eureka") as class_2561)
         .method_47320({ 
            class_1799(EurekaBlocks.INSTANCE.OAK_SHIP_HELM.get().method_8389() as class_1935)
         })
         .method_47317({ var0: class_8128, output: class_7704 ->
            for (`element$iv` in EurekaItems.INSTANCE.ITEMS) {
               output.method_45421((`element$iv` as RegistrySupplier).get() as class_1935)
            }
         })
         .method_47324()
         return var10000
   }
}
