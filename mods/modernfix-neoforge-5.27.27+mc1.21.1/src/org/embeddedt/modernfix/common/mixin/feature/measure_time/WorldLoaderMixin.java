package org.embeddedt.modernfix.common.mixin.feature.measure_time;

import com.google.common.base.Stopwatch;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.WorldLoader;
import org.embeddedt.modernfix.ModernFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
   @Inject(method = "load", at = @At("HEAD"))
   private static void startStopwatch(CallbackInfoReturnable<CompletableFuture<?>> cir, @Share("stopwatch") LocalRef<Stopwatch> stopwatch) {
      stopwatch.set(Stopwatch.createStarted());
   }

   @ModifyReturnValue(method = "load", at = @At("RETURN"))
   private static CompletableFuture<?> finishStopwatch(CompletableFuture<?> original, @Share("stopwatch") LocalRef<Stopwatch> stopwatch) {
      Stopwatch watch = (Stopwatch)stopwatch.get();
      return watch != null ? original.whenComplete((o, throwable) -> {
         watch.stop();
         ModernFix.LOGGER.warn("Initial datapack load took {}", watch);
      }) : original;
   }
}
