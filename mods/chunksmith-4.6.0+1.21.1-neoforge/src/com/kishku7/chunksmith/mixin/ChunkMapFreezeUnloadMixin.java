package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.worldenter.WorldEnterPregen;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChunkMap.class)
public abstract class ChunkMapFreezeUnloadMixin {
   @ModifyArg(
      method = "tick(Ljava/util/function/BooleanSupplier;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;processUnloads(Ljava/util/function/BooleanSupplier;)V"),
      require = 0
   )
   private BooleanSupplier chunksmith$budgetUnloadsWhileFrozen(BooleanSupplier haveTime) {
      if (!WorldEnterPregen.isFrozen()) {
         return haveTime;
      }

      long deadline = System.nanoTime() + 10000000L;
      return () -> System.nanoTime() < deadline && haveTime.getAsBoolean();
   }
}
