package org.embeddedt.modernfix.common.mixin.perf.encoder_cache_leak;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
   @ModifyReturnValue(method = "loadResources", at = @At("RETURN"))
   private static CompletableFuture<ReloadableServerResources> resetEncoderCache(CompletableFuture<ReloadableServerResources> future) {
      return future.whenComplete((r, t) -> ((EncoderCacheAccessor)DataComponentsAccessor.mfix$getCache()).mfix$getCache().invalidateAll());
   }
}
