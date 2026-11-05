package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.lod.LodSupport;
import maki.chunksmithcache.CacheManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LodSupport.class, remap = false)
public abstract class LodSupportMixin {
   @Inject(method = "offer", at = @At("TAIL"))
   private static void maki$cacheDiscarded(ServerLevel var0, LevelChunk var1, CallbackInfo var2) {
      CacheManager.captureFromChunksmith(var0, var1);
   }
}
