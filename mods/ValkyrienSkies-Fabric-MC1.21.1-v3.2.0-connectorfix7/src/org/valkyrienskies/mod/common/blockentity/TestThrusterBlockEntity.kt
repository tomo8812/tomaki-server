package org.valkyrienskies.mod.common.blockentity

import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

public class TestThrusterBlockEntity(blockPos: class_2338, blockState: class_2680) : class_2586(
         ValkyrienSkiesMod.INSTANCE.TEST_THRUSTER_BLOCK_ENTITY_TYPE, blockPos, blockState
      ),
   BlockEntityPhysicsListener {
   public open lateinit var dimension: String
   public final var isActive: Boolean

   init {
      val var10001: java.lang.Comparable = blockState.method_11654(class_2741.field_12484 as class_2769)
      this.isActive = var10001 as java.lang.Boolean
   }

   public final val facing: class_2350
      public final get() {
         val var10000: java.lang.Comparable = this.method_11010().method_11654(class_2741.field_12525 as class_2769)
         return var10000 as class_2350
      }


   public final val pos: class_2338
      public final get() {
         val var10000: class_2338 = this.field_11867
         return var10000
      }


   public override fun physTick(physShip: PhysShip?, physLevel: PhysLevel) {
      if (this.isActive && physShip != null) {
         val var10001: class_2382 = this.facing.method_10163()
         val var3: Vector3d = VectorConversionsMCKt.toJOMLD(var10001).mul(100000.0)
         val var4: Vector3dc = var3
         val var10002: Vector3d = VectorConversionsMCKt.toJOMLD(this.pos as class_2382).add(0.5, 0.5, 0.5)
         physShip.applyModelForce(var4, var10002)
      }
   }
}
