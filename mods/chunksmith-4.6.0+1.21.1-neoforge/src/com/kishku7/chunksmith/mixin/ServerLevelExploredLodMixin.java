package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.lod.ExploredLodGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelExploredLodMixin {
   @Inject(method = "unload(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("HEAD"))
   private void chunksmith$exploredLodOnUnload(LevelChunk chunk, CallbackInfo ci) {
      ExploredLodGuard.onUnload((ServerLevel)this, chunk);
   }
}
