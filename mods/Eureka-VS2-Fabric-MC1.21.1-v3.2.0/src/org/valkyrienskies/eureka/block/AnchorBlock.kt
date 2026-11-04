package org.valkyrienskies.eureka.block

import com.mojang.serialization.MapCodec
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1750
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2383
import net.minecraft.class_2470
import net.minecraft.class_2498
import net.minecraft.class_265
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_3620
import net.minecraft.class_3726
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShape
import org.valkyrienskies.eureka.util.RotShapes
import org.valkyrienskies.mod.common.VSGameUtilsKt

@SourceDebugExtension(["SMAP\nAnchorBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AnchorBlock.kt\norg/valkyrienskies/eureka/block/AnchorBlock\n+ 2 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n*L\n1#1,124:1\n66#2:125\n*S KotlinDebug\n*F\n+ 1 AnchorBlock.kt\norg/valkyrienskies/eureka/block/AnchorBlock\n*L\n112#1:125\n*E\n"])
public class AnchorBlock : class_2383(
      class_2251.method_9637().method_31710(class_3620.field_16005).method_9629(5.0F, 6.0F).method_9626(class_2498.field_11531).method_29292()
   ) {
   public final val ANCHOR_BOTTOM: RotShape = RotShapes.INSTANCE.box(2.0, 2.0, 14.0, 14.0, 4.0, 16.0)
   public final val ANCHOR_ROD: RotShape = RotShapes.INSTANCE.box(7.0, 2.0, 14.0, 9.0, 24.0, 16.0)
   public final val ANCHOR_SHAPE: DirectionalShape = DirectionalShape(RotShapes.INSTANCE.or(this.ANCHOR_BOTTOM, this.ANCHOR_ROD))

   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      builder.method_11667(arrayOf(class_2383.field_11177)).method_11667(arrayOf(class_2741.field_12484))
   }

   fun method_9605(ctx: class_1750): class_2680? {
      (this.method_9564().method_11657(class_2383.field_11177 as class_2769, ctx.method_8042().method_10153() as java.lang.Comparable) as class_2680)
         .method_11657(class_2741.field_12484 as class_2769, ctx.method_8045().method_49803(ctx.method_8037())) as class_2680
   }

   fun method_9598(state: class_2680, rotation: class_2470): class_2680? {
      val var10001: class_2769 = class_2383.field_11177 as class_2769
      val var10003: java.lang.Comparable = state.method_11654(class_2383.field_11177 as class_2769)
      val var10000: Any = state.method_11657(var10001, rotation.method_10503(var10003 as class_2350) as java.lang.Comparable)
      var10000 as class_2680
   }

   fun method_9530(blockState: class_2680, blockGetter: class_1922, blockPos: class_2338, collisionContext: class_3726): class_265 {
      val var10000: DirectionalShape = this.ANCHOR_SHAPE
      val var10001: java.lang.Comparable = blockState.method_11654(class_2383.field_11177 as class_2769)
      var10000.get(var10001 as class_2350)
   }

   fun method_9612(state: class_2680, level: class_1937, pos: class_2338, block: class_2248, fromPos: class_2338, isMoving: Boolean) {
      if (!level.field_9236) {
         val bl: Boolean = level.method_49803(pos)
         if (!(bl == state.method_11654(class_2741.field_12484 as class_2769) as java.lang.Boolean)) {
            (level as class_3218).method_8652(pos, state.method_11657(class_2741.field_12484 as class_2769, bl) as class_2680, 11)
         }

         super.method_9612(state, level, pos, block, fromPos, isMoving)
      }
   }

   fun method_9615(state: class_2680, level: class_1937, pos: class_2338, oldState: class_2680, isMoving: Boolean) {
      super.method_9615(state, level, pos, oldState, isMoving)
      if (!level.field_9236) {
         val bl: java.lang.Boolean = state.method_11654(class_2741.field_12484 as class_2769) as java.lang.Boolean
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val attachment: EurekaShipControl = EurekaShipControl.Companion.getOrCreate(var10000)
            attachment.anchors = attachment.anchors + 1
            attachment.anchorsActive = attachment.anchorsActive + (if (bl) 1 else 0)
         }
      }
   }

   fun method_9536(state: class_2680, level: class_1937, pos: class_2338, newState: class_2680, isMoving: Boolean) {
      super.method_9536(state, level, pos, newState, isMoving)
      if (!level.field_9236) {
         val bl: java.lang.Boolean = state.method_11654(class_2741.field_12484 as class_2769) as java.lang.Boolean
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var11: EurekaShipControl = (var10000 as AttachmentHolder).getAttachment(EurekaShipControl.class) as EurekaShipControl
            if (var11 != null) {
               var11.anchors = var11.anchors - 1
               var11.anchorsActive = var11.anchorsActive - (if (bl) 1 else 0)
            }
         }
      }
   }

   fun method_53969(): MapCodec<AnchorBlock> {
      CODEC
   }

   @JvmStatic
   fun {
      val var10000: MapCodec = class_2383.method_54094({ it: class_2251 ->
         AnchorBlock()
      })
      CODEC = var10000
   }

   public companion object {
      public final val CODEC: MapCodec<AnchorBlock>
   }
}
