package org.valkyrienskies.mod.mixin.mod_compat.immersive_portals;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_3218;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import qouteall.imm_ptl.core.chunk_loading.ChunkLoader;
import qouteall.imm_ptl.core.chunk_loading.ImmPtlChunkTracking;
import qouteall.imm_ptl.core.chunk_loading.ChunkLoader.ChunkPosConsumer;

@Mixin(ImmPtlChunkTracking.class)
public class MixinImmPtlChunkTracking {
   @Redirect(
      method = "updateForPlayer",
      at = @At(
         value = "INVOKE",
         target = "Lqouteall/imm_ptl/core/chunk_loading/ChunkLoader;foreachChunkPos(Lqouteall/imm_ptl/core/chunk_loading/ChunkLoader$ChunkPosConsumer;)V"
      )
   )
   private static void addShipChunks(ChunkLoader instance, ChunkPosConsumer func, @Local class_3218 world) {
      for (int dx = -instance.radius(); dx <= instance.radius(); dx++) {
         for (int dz = -instance.radius(); dz <= instance.radius(); dz++) {
            func.consume(instance.getCenter().dimension, instance.getCenter().x + dx, instance.getCenter().z + dz, Math.max(Math.abs(dx), Math.abs(dz)));
         }
      }

      AABBd box = new AABBd(
         instance.getCenter().x - instance.radius() << 4,
         world.method_31607(),
         instance.getCenter().z - instance.radius() << 4,
         instance.getCenter().x + instance.radius() << 4,
         world.method_31600(),
         instance.getCenter().z + instance.radius() << 4
      );

      for (Ship ship : VSGameUtilsKt.getShipsIntersecting(world, box)) {
         ship.getActiveChunksSet().forEach((x, z) -> func.consume(instance.getCenter().dimension, x, z, 1));
      }
   }
}
