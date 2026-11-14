package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.lod.HorizonGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityStorage.class)
public abstract class EntityStorageHorizonMixin {
   @Shadow
   @Final
   private ServerLevel level;

   @Inject(method = "storeEntities(Lnet/minecraft/world/level/entity/ChunkEntities;)V", at = @At("HEAD"), cancellable = true)
   private void chunksmith$horizonVeto(ChunkEntities<Entity> entities, CallbackInfo ci) {
      if (HorizonGuard.armed() && HorizonGuard.drop(this.level, entities.getPos())) {
         ci.cancel();
      }
   }
}
