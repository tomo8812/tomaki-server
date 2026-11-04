package org.embeddedt.modernfix.common.mixin.feature.mcfunction_profiling;

import com.google.common.base.Stopwatch;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import org.embeddedt.modernfix.duck.IProfilingServerFunctionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerFunctionManager.class)
public class ServerFunctionManagerMixin implements IProfilingServerFunctionManager {
   @Shadow
   @Final
   private static ResourceLocation TICK_FUNCTION_TAG;
   private final Map<ResourceLocation, Stopwatch> mfix$functionWatches = new Object2ObjectOpenHashMap();

   @Inject(method = "executeTagFunctions", at = @At("HEAD"))
   private void resetWatches(Collection<CommandFunction<CommandSourceStack>> functionObjects, ResourceLocation identifier, CallbackInfo ci) {
      this.mfix$functionWatches.values().forEach(Stopwatch::reset);
   }

   @Inject(
      method = "executeTagFunctions",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/ServerFunctionManager;execute(Lnet/minecraft/commands/functions/CommandFunction;Lnet/minecraft/commands/CommandSourceStack;)V"
      )
   )
   private void startWatch(
      Collection<CommandFunction<CommandSourceStack>> functionObjects,
      ResourceLocation identifier,
      CallbackInfo ci,
      @Local(ordinal = 0) CommandFunction<CommandSourceStack> function,
      @Share("stopwatch") LocalRef<Stopwatch> watchRef
   ) {
      watchRef.set(null);
      if (identifier == TICK_FUNCTION_TAG) {
         Stopwatch watch = this.mfix$functionWatches.computeIfAbsent(function.id(), i -> Stopwatch.createUnstarted());
         watch.start();
         watchRef.set(watch);
      }
   }

   @Inject(
      method = "executeTagFunctions",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/ServerFunctionManager;execute(Lnet/minecraft/commands/functions/CommandFunction;Lnet/minecraft/commands/CommandSourceStack;)V",
         shift = Shift.AFTER
      )
   )
   private void stopWatch(
      Collection<CommandFunction<CommandSourceStack>> functionObjects,
      ResourceLocation identifier,
      CallbackInfo ci,
      @Share("stopwatch") LocalRef<Stopwatch> watchRef
   ) {
      Stopwatch watch = (Stopwatch)watchRef.get();
      if (watch != null && watch.isRunning()) {
         watch.stop();
      }
   }

   @Inject(method = "executeTagFunctions", at = @At("RETURN"))
   private void pruneUnusedWatches(Collection<CommandFunction<CommandSourceStack>> functionObjects, ResourceLocation identifier, CallbackInfo ci) {
      this.mfix$functionWatches.values().removeIf(watch -> watch.elapsed().isZero());
   }

   @Override
   public String mfix$getProfilingResults() {
      ArrayList<Entry<ResourceLocation, Stopwatch>> list = new ArrayList<>(this.mfix$functionWatches.entrySet());
      list.sort(Comparator.<Entry<ResourceLocation, Stopwatch>, Duration>comparing(e -> e.getValue().elapsed()).reversed());
      StringBuilder sb = new StringBuilder();

      for (Entry<ResourceLocation, Stopwatch> entry : list) {
         sb.append(entry.getKey().toString());
         sb.append(" - ");
         sb.append(entry.getValue().toString());
         sb.append('\n');
      }

      return sb.toString();
   }
}
