package org.valkyrienskies.mod.mixin.server.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3222;
import net.minecraft.class_3898;
import net.minecraft.class_3898.class_3208;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_3208.class)
public class MixinChunkMap$TrackedEntity {
   @Shadow
   @Final
   class_1297 field_18247;
   @Unique
   private Ship valkyrienskies$inCallShip = null;

   @WrapOperation(method = "updatePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;position()Lnet/minecraft/world/phys/Vec3;"))
   class_243 includeShips(class_1297 instance, Operation<class_243> operation) {
      class_243 original = (class_243)operation.call(new Object[]{instance});
      Vector3d pos = VectorConversionsMCKt.toJOML(original);
      Ship ship = this.valkyrienskies$inCallShip = VSGameUtilsKt.getShipObjectManagingPos(this.field_18247.method_37908(), pos);
      return ship != null ? VectorConversionsMCKt.toMinecraft(ship.getShipToWorld().transformPosition(pos)) : original;
   }

   @WrapOperation(
      method = "updatePlayer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;broadcastToPlayer(Lnet/minecraft/server/level/ServerPlayer;)Z")
   )
   boolean skipWierdCheck(class_1297 instance, class_3222 serverPlayer, Operation<Boolean> broadcastToPlayer) {
      return this.valkyrienskies$inCallShip != null || (Boolean)broadcastToPlayer.call(new Object[]{instance, serverPlayer});
   }

   @WrapOperation(
      method = "updatePlayer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;isChunkTracked(Lnet/minecraft/server/level/ServerPlayer;II)Z")
   )
   boolean skipWierdCheck2(class_3898 instance, class_3222 serverPlayer, int i, int j, Operation<Boolean> original) {
      return this.valkyrienskies$inCallShip != null || (Boolean)original.call(new Object[]{instance, serverPlayer, i, j});
   }
}
