package org.valkyrienskies.mod.common.item

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1269
import net.minecraft.class_1657
import net.minecraft.class_1792
import net.minecraft.class_1799
import net.minecraft.class_1838
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2561
import net.minecraft.class_2680
import net.minecraft.class_3218
import net.minecraft.class_1792.class_1793
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.api.ValkyrienSkies
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.assembly.ShipAssembler

@SourceDebugExtension(["SMAP\nShipAssemblerItem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipAssemblerItem.kt\norg/valkyrienskies/mod/common/item/ShipAssemblerItem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,49:1\n1557#2:50\n1628#2,3:51\n*S KotlinDebug\n*F\n+ 1 ShipAssemblerItem.kt\norg/valkyrienskies/mod/common/item/ShipAssemblerItem\n*L\n39#1:50\n39#1:51,3\n*E\n"])
public class ShipAssemblerItem(properties: class_1793) : class_1792(properties) {
   fun method_7886(stack: class_1799): Boolean {
      true
   }

   fun method_7884(ctx: class_1838): class_1269 {
      val blockState: class_1937 = ctx.method_8045()
      val var10000: class_3218 = blockState as? class_3218
      if ((blockState as? class_3218) == null) {
         val var27: class_1269 = super.method_7884(ctx)
         var27
      } else {
         val pos: class_2338 = ctx.method_8037()
         val var22: class_2680 = var10000.method_8320(pos)
         if (!var10000.field_9236) {
            val var23: class_1937 = ctx.method_8045()
            if (VSGameUtilsKt.isChunkInShipyard(var23, pos.method_10263() shr 4, pos.method_10260() shr 4)) {
               val var24: class_1657 = ctx.method_8036()
               if (var24 != null) {
                  var24.method_43496(class_2561.method_43470("That chunk is already part of a ship!") as class_2561)
               }
            } else if (!var22.method_26215()) {
               val set: DenseBlockPosSet = DenseBlockPosSet()

               for (shipData in -3..3) {
                  for (`$this$map$iv` in -3..3) {
                     set.add(pos.method_10263() + shipData, pos.method_10264(), pos.method_10260() + `$this$map$iv`)
                  }
               }

               val var20: java.lang.Iterable = set
               val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(set, 10))

               for (`item$iv$iv` in var20) {
                  val it: Vector3ic = `item$iv$iv` as Vector3ic
                  `destination$iv$iv`.add(ValkyrienSkies.toBlockPos(it))
               }

               val var19: ServerShip = ShipAssembler.assembleToShip(var10000, CollectionsKt.toSet(`destination$iv$iv` as java.util.List), 1.0)
               val var25: class_1657 = ctx.method_8036()
               if (var25 != null) {
                  var25.method_43496(class_2561.method_43469("command.valkyrienskies.shipify.success_one", arrayOf(var19.slug)) as class_2561)
               }
            }
         }

         val var26: class_1269 = super.method_7884(ctx)
         var26
      }
   }
}
