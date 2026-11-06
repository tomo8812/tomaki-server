package maki.millenairelod.mixin;

import com.kishku7.chunksmith.lod.HorizonGuard;
import maki.millenairelod.Compat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HorizonGuard.class, remap = false)
public abstract class HorizonGuardMixin {
   @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
   private static void compat$keepVillage(ServerLevel level, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (HorizonGuard.armed() && Compat.protectedChunk(level, pos)) {
         cir.setReturnValue(false);
      }
   }
}
