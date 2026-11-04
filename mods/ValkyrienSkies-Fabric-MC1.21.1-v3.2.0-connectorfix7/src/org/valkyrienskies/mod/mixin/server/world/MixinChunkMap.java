package org.valkyrienskies.mod.mixin.server.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.class_1297;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_26;
import net.minecraft.class_2791;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3898;
import net.minecraft.class_3898.class_3216;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.MinecraftPlayer;

@Mixin(value = class_3898.class, priority = 1100)
public abstract class MixinChunkMap {
   @Shadow
   @Final
   class_3218 field_17214;
   @Shadow
   @Final
   private Supplier<class_26> field_17705;

   @Inject(method = "getPlayers", at = @At("RETURN"), cancellable = true)
   private void postGetPlayersWatchingChunk(class_1923 chunkPos, boolean onlyOnWatchDistanceEdge, CallbackInfoReturnable<List<class_3222>> cir) {
      Iterator<VsiPlayer> playersWatchingShipChunk = VSGameUtilsKt.getShipObjectWorld(this.field_17214)
         .getIPlayersWatchingShipChunk(chunkPos.field_9181, chunkPos.field_9180, VSGameUtilsKt.getDimensionId(this.field_17214));
      if (playersWatchingShipChunk.hasNext()) {
         List<class_3222> oldReturnValue = (List<class_3222>)cir.getReturnValue();
         Set<class_3222> watchingPlayers = new HashSet<>(oldReturnValue);
         playersWatchingShipChunk.forEachRemaining(iPlayer -> {
            if (iPlayer instanceof MinecraftPlayer minecraftPlayer) {
               class_3222 playerEntity = (class_3222)minecraftPlayer.getPlayerEntityReference().get();
               if (playerEntity != null) {
                  watchingPlayers.add(playerEntity);
               }
            }
         });
         cir.setReturnValue(new ArrayList<>(watchingPlayers));
      }
   }

   @WrapOperation(
      method = "anyPlayerCloseEnoughForSpawning",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap$DistanceManager;hasPlayersNearby(J)Z")
   )
   private boolean onHasPlayersNearby(class_3216 instance, long l, Operation<Boolean> original, @Local(argsOnly = true) class_1923 arg) {
      return (Boolean)original.call(
         new Object[]{
            instance, new class_1923(class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.field_17214, arg.method_33943(63)))).method_8324()
         }
      );
   }

   @WrapOperation(
      method = "playerIsCloseEnoughForSpawning",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ChunkMap;euclideanDistanceSquared(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/entity/Entity;)D"
      )
   )
   private double onEuclideanDistanceSquared(class_1923 d0, class_1297 d1, Operation<Double> original) {
      return (Double)original.call(
         new Object[]{new class_1923(class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.field_17214, d0.method_33943(63)))), d1}
      );
   }

   @Inject(method = "save", at = @At("HEAD"), cancellable = true)
   private void preSave(class_2791 chunkAccess, CallbackInfoReturnable<Boolean> cir) {
      class_1923 pos = chunkAccess.method_12004();
      Ship ship = VSGameUtilsKt.getShipManagingPos(this.field_17214, pos);
      if (ship != null && !ship.getActiveChunksSet().contains(pos.field_9181, pos.field_9180)) {
         cir.setReturnValue(false);
      }
   }
}
