package maki.chunksmithcache.mixin;

import maki.chunksmithcache.BackfillController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelUnloadMixin {
   @Inject(method = "unload(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("TAIL"))
   private void maki$backfillUnloaded(LevelChunk var1, CallbackInfo var2) {
      BackfillController.onUnload((ServerLevel)(Object)this, var1);
   }
}
