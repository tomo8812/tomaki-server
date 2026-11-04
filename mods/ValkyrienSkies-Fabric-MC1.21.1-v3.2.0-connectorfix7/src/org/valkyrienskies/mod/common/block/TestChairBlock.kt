package org.valkyrienskies.mod.common.block

import com.mojang.serialization.MapCodec
import net.minecraft.class_1269
import net.minecraft.class_1297
import net.minecraft.class_1657
import net.minecraft.class_1750
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2383
import net.minecraft.class_2498
import net.minecraft.class_265
import net.minecraft.class_2680
import net.minecraft.class_2769
import net.minecraft.class_3726
import net.minecraft.class_3965
import net.minecraft.class_2183.class_2184
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

public class TestChairBlock : class_2383(class_2251.method_9637().method_9629(1.0F, 120.0F).method_9626(class_2498.field_11543)) {
   private final val SEAT_AABB: class_265

   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      builder.method_11667(arrayOf(class_2383.field_11177))
   }

   fun method_9605(ctx: class_1750): class_2680? {
      this.method_9564().method_11657(class_2383.field_11177 as class_2769, ctx.method_8042().method_10153() as java.lang.Comparable) as class_2680
   }

   fun method_9530(state: class_2680, level: class_1922, pos: class_2338, context: class_3726): class_265 {
      this.SEAT_AABB
   }

   fun method_55766(state: class_2680, level: class_1937, pos: class_2338, player: class_1657, blockHitResult: class_3965): class_1269 {
      if (level.field_9236) {
         class_1269.field_5812
      } else {
         val var10000: class_1297 = ValkyrienSkiesMod.INSTANCE.SHIP_MOUNTING_ENTITY_TYPE.method_5883(level)
         val `$this$useWithoutItem_u24lambda_u240`: ShipMountingEntity = var10000 as ShipMountingEntity
         val seatEntityPos: Vector3dc = Vector3d(pos.method_10263() + 0.5, pos.method_10264() + 0.15, pos.method_10260() + 0.5)
         `$this$useWithoutItem_u24lambda_u240`.method_24203(x, y, z)
         val var10001: class_2184 = class_2184.field_9851
         val var10002: class_2382 = (state.method_11654(class_2383.field_11177 as class_2769) as class_2350).method_10163()
         `$this$useWithoutItem_u24lambda_u240`.method_5702(
            var10001, VectorConversionsMCKt.toDoubles(var10002).method_1019(`$this$useWithoutItem_u24lambda_u240`.method_19538())
         )
         `$this$useWithoutItem_u24lambda_u240`.isController = true
         val seatEntity: ShipMountingEntity = var10000 as ShipMountingEntity
         level.method_8649(var10000 as ShipMountingEntity)
         player.method_5804(seatEntity)
         class_1269.field_21466
      }
   }

   fun method_53969(): MapCodec<TestChairBlock> {
      CODEC
   }

   @JvmStatic
   fun {
      val var10000: MapCodec = class_2383.method_54094({ it: class_2251 ->
         TestChairBlock()
      })
      CODEC = var10000
   }

   public companion object {
      public final val CODEC: MapCodec<TestChairBlock>
   }
}
