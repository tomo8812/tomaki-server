package org.valkyrienskies.mod.mixin.feature.tick_ship_chunks;

import net.minecraft.class_1297;
import net.minecraft.class_1923;
import net.minecraft.class_3218;
import net.minecraft.class_3898;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_3898.class)
public abstract class MixinChunkMap {
   @Shadow
   @Final
   class_3218 field_17214;

   @Inject(method = "euclideanDistanceSquared", at = @At("HEAD"), cancellable = true)
   private static void preDistanceToSqr(class_1923 chunkPos, class_1297 entity, CallbackInfoReturnable<Double> cir) {
      double d = chunkPos.field_9181 * 16 + 8;
      double e = chunkPos.field_9180 * 16 + 8;
      double retValue = VSGameUtilsKt.squaredDistanceBetweenInclShips(entity.method_37908(), entity.method_23317(), 0.0, entity.method_23321(), d, 0.0, e);
      cir.setReturnValue(retValue);
   }

   @Inject(method = "anyPlayerCloseEnoughForSpawning", at = @At("RETURN"), cancellable = true)
   void noPlayersCloseForSpawning(class_1923 chunkPos, CallbackInfoReturnable<Boolean> cir) {
      if (VSGameUtilsKt.isChunkInShipyard(this.field_17214, chunkPos.field_9181, chunkPos.field_9180) && !(Boolean)cir.getReturnValue()) {
         ServerShip ship = VSGameUtilsKt.getShipObjectWorld(this.field_17214)
            .getLoadedShips()
            .getByChunkPos(chunkPos.field_9181, chunkPos.field_9180, VSGameUtilsKt.getDimensionId(this.field_17214));
         if (ship != null) {
            cir.setReturnValue(true);
         }
      }
   }
}
