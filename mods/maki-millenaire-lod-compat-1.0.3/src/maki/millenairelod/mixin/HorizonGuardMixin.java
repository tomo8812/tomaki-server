package maki.millenairelod.mixin;

import com.kishku7.chunksmith.lod.HorizonGuard;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodSupport;
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
   private static void compat$keepVillage(ServerLevel var0, ChunkPos var1, CallbackInfoReturnable<Boolean> var2) {
      if (var0 != null && LodHorizon.isArmed(LodSupport.dimensionId(var0)) && Compat.protectedChunk(var0, var1)) {
         var2.setReturnValue(false);
      }
   }
}
