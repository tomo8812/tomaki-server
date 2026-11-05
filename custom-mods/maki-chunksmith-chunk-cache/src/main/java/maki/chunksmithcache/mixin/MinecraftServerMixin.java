package maki.chunksmithcache.mixin;

import java.util.function.BooleanSupplier;
import maki.chunksmithcache.BackfillController;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
   @Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
   private void maki$backfillTick(BooleanSupplier var1, CallbackInfo var2) {
      BackfillController.tick((MinecraftServer)(Object)this);
   }
}
