package maki.millenairelod.mixin;

import com.kishku7.chunksmith.lod.ExploredLodGuard;
import maki.millenairelod.Compat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExploredLodGuard.class, remap = false)
public abstract class ExploredLodMixin {
   @Inject(method = "onSave", at = @At("HEAD"))
   private static void compat$refreshSave(ServerLevel var0, ChunkAccess var1, CallbackInfo var2) {
      Compat.refresh(var0, var1);
   }

   @Inject(method = "onUnload", at = @At("HEAD"))
   private static void compat$refreshUnload(ServerLevel var0, LevelChunk var1, CallbackInfo var2) {
      Compat.refresh(var0, var1);
   }
}
