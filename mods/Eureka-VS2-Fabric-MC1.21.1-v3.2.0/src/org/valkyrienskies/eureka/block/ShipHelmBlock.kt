package org.valkyrienskies.eureka.block

import com.mojang.datafixers.kinds.App
import com.mojang.datafixers.kinds.Applicative
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_10
import net.minecraft.class_1269
import net.minecraft.class_1297
import net.minecraft.class_1657
import net.minecraft.class_1750
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2237
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2464
import net.minecraft.class_2470
import net.minecraft.class_2561
import net.minecraft.class_2586
import net.minecraft.class_2591
import net.minecraft.class_265
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_3726
import net.minecraft.class_3965
import net.minecraft.class_5558
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShape
import org.valkyrienskies.eureka.util.RotShapes
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

@SourceDebugExtension(["SMAP\nShipHelmBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipHelmBlock.kt\norg/valkyrienskies/eureka/block/ShipHelmBlock\n+ 2 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n*L\n1#1,157:1\n66#2:158\n*S KotlinDebug\n*F\n+ 1 ShipHelmBlock.kt\norg/valkyrienskies/eureka/block/ShipHelmBlock\n*L\n65#1:158\n*E\n"])
public class ShipHelmBlock(properties: class_2251, woodType: IWoodType) : class_2237(properties) {
   public final val woodType: IWoodType
   public final val HELM_BASE: RotShape
   public final val HELM_POLE: RotShape
   public final val HELM_SHAPE: DirectionalShape

   init {
      this.woodType = woodType
      this.HELM_BASE = RotShapes.INSTANCE.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0)
      this.HELM_POLE = RotShapes.INSTANCE.box(4.0, 2.0, 5.0, 12.0, 13.0, 13.0)
      this.HELM_SHAPE = DirectionalShape(RotShapes.INSTANCE.or(this.HELM_BASE, this.HELM_POLE))
      this.method_9590(
         (this.field_10647.method_11664() as class_2680).method_11657(class_2741.field_12481 as class_2769, class_2350.field_11043 as java.lang.Comparable) as class_2680
      )
   }

   fun method_9615(state: class_2680, level: class_1937, pos: class_2338, oldState: class_2680, isMoving: Boolean) {
      super.method_9615(state, level, pos, oldState, isMoving)
      if (!level.field_9236) {
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var7: EurekaShipControl = EurekaShipControl.Companion.getOrCreate(var10000)
            var7.helms = var7.helms + 1
         }
      }
   }

   fun method_9536(state: class_2680, level: class_1937, pos: class_2338, newState: class_2680, isMoving: Boolean) {
      super.method_9536(state, level, pos, newState, isMoving)
      if (!level.field_9236) {
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var10: EurekaShipControl = (var10000 as AttachmentHolder).getAttachment(EurekaShipControl.class) as EurekaShipControl
            if (var10 != null) {
               if (var10.helms <= 1) {
                  run label43@{
                     val var11: class_1657 = var10.seatedPlayer
                     if (var11 != null) {
                        val var12: class_1297 = var11.method_5854()
                        if (var12 != null) {
                           var13 = var12.method_5864()
                           return@label43
                        }
                     }

                     var13 = null
                  }

                  if (var13 == ValkyrienSkiesMod.INSTANCE.getSHIP_MOUNTING_ENTITY_TYPE()) {
                     val var14: class_1657 = var10.seatedPlayer
                     var14.method_18375()
                     var10.seatedPlayer = null
                  }
               }

               var10.helms = var10.helms - 1
            }
         }
      }
   }

   fun method_55766(state: class_2680, level: class_1937, pos: class_2338, player: class_1657, blockHitResult: class_3965): class_1269 {
      if (level.field_9236) {
         class_1269.field_5812
      } else {
         val var10000: class_2586 = level.method_8321(pos)
         val blockEntity: ShipHelmBlockEntity = var10000 as ShipHelmBlockEntity
         val var7: class_1269
         if (player.method_21823()) {
            player.method_17355(blockEntity)
            var7 = class_1269.field_21466
         } else if (VSGameUtilsKt.getShipManagingPos(level, pos) == null) {
            player.method_7353(class_2561.method_43471("info.vs_eureka.sneak_to_open_helm") as class_2561, true)
            var7 = class_1269.field_21466
         } else {
            var7 = if (ShipHelmBlockEntity.sit$default(blockEntity, player, false, 2, null)) class_1269.field_21466 else class_1269.field_5811
         }

         var7
      }
   }

   fun method_9604(blockState: class_2680): class_2464 {
      class_2464.field_11458
   }

   fun method_9605(ctx: class_1750): class_2680? {
      this.method_9564().method_11657(class_2741.field_12481 as class_2769, ctx.method_8042().method_10153() as java.lang.Comparable) as class_2680
   }

   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      builder.method_11667(arrayOf(class_2741.field_12481))
   }

   fun method_10123(blockPos: class_2338, state: class_2680): class_2586 {
      ShipHelmBlockEntity(blockPos, state) as class_2586
   }

   fun method_9530(blockState: class_2680, blockGetter: class_1922, blockPos: class_2338, collisionContext: class_3726): class_265 {
      val var10000: DirectionalShape = this.HELM_SHAPE
      val var10001: java.lang.Comparable = blockState.method_11654(class_2741.field_12481 as class_2769)
      var10000.get(var10001 as class_2350)
   }

   fun method_9526(blockState: class_2680): Boolean {
      true
   }

   fun method_9516(blockState: class_2680, pathComputationType: class_10): Boolean {
      false
   }

   fun method_9598(state: class_2680, rotation: class_2470): class_2680? {
      val var10001: class_2769 = class_2741.field_12481 as class_2769
      val var10003: java.lang.Comparable = state.method_11654(class_2741.field_12481 as class_2769)
      val var10000: Any = state.method_11657(var10001, rotation.method_10503(var10003 as class_2350) as java.lang.Comparable)
      var10000 as class_2680
   }

   fun <T extends class_2586> method_31645(level: class_1937, state: class_2680, type: class_2591<T>): class_5558<T> {
      { level: class_1937, pos: class_2338, state: class_2680, blockEntity: class_2586 ->
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
         //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
         //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(ReferencePipeline.java:288)
         //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1716)
         //   at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:570)
         //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
         //   at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:635)
         //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(AbstractPipeline.java:291)
         //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:652)
         //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:658)
         //   at java.base/java.util.stream.ReferencePipeline.toList(ReferencePipeline.java:663)
         //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
         //   at org.vineflower.kotlin.expr.KExitExprent.toJava(KExitExprent.java:41)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      }
   }

   fun method_53969(): MapCodec<ShipHelmBlock> {
      CODEC
   }

   @JvmStatic
   fun {
      val var10000: MapCodec = RecordCodecBuilder.mapCodec({ instance: Instance ->
         instance.group(class_2237.method_54096() as App, WoodType.Companion.CODEC.fieldOf("wood_type").forGetter({ it: ShipHelmBlock ->
            val var10000: IWoodType = it.woodType
            var10000 as WoodType
         }) as App).apply(instance as Applicative, org/valkyrienskies/eureka/block/ShipHelmBlock##Lambda_2_543())
      })
      CODEC = var10000
   }

   public companion object {
      public final val CODEC: MapCodec<ShipHelmBlock>
   }
}
