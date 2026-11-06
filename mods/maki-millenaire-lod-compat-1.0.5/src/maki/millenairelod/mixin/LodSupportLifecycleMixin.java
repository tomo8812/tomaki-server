package maki.millenairelod.mixin;

import com.kishku7.chunksmith.lod.LodSupport;
import maki.millenairelod.LodRefreshQueue;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LodSupport.class, remap = false)
public abstract class LodSupportLifecycleMixin {
   @Inject(method = "install", at = @At("TAIL"))
   private static void maki$startRefreshWorker(MinecraftServer server, CallbackInfo ci) {
      LodRefreshQueue.start(server);
   }

   @Inject(method = "shutdown", at = @At("HEAD"))
   private static void maki$drainRefreshWorker(CallbackInfo ci) {
      LodRefreshQueue.shutdown();
   }
}
