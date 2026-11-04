package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.util.FrozenTicketPurge;
import com.kishku7.chunksmith.util.HeapPressure;
import com.kishku7.chunksmith.worldenter.WorldEnterPregen;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheFreezeMixin {
   @Unique
   private static final Logger chunksmith$LOGGER = LoggerFactory.getLogger("Chunksmith");
   @Unique
   private static volatile boolean chunksmith$announced;
   @Unique
   private static volatile boolean chunksmith$disabled;
   @Unique
   private final FrozenTicketPurge chunksmith$policy = new FrozenTicketPurge();
   @Shadow
   @Final
   ServerLevel level;
   @Shadow
   @Final
   private DistanceManager distanceManager;

   @Inject(method = "tick(Ljava/util/function/BooleanSupplier;Z)V", at = @At("HEAD"))
   private void chunksmith$purgeWhileFrozen(BooleanSupplier hasTimeLeft, boolean tickChunks, CallbackInfo ci) {
      if (!chunksmith$disabled && tickChunks) {
         if (WorldEnterPregen.isFrozen() && !this.level.tickRateManager().runsNormally()) {
            int resident = ((ServerChunkCache)this).getLoadedChunksCount();
            boolean heapFull = HeapPressure.isHolding() && HeapPressure.afterCollectionPercent() >= 70.0;
            int learnedBefore = this.chunksmith$policy.learnedCap();
            boolean purge = this.chunksmith$policy.tick(resident, Runtime.getRuntime().maxMemory(), heapFull);
            if (this.chunksmith$policy.learnedCap() != learnedBefore) {
               chunksmith$LOGGER.info(
                  "Chunksmith: the heap is full with {} chunks cached during the world-enter freeze; caching at most {} from now on.",
                  resident,
                  this.chunksmith$policy.learnedCap()
               );
            }

            if (purge) {
               try {
                  this.chunksmith$purge();
               } catch (Throwable t) {
                  chunksmith$disabled = true;
                  chunksmith$LOGGER.warn(
                     "Chunksmith: could not expire chunk tickets during the world-enter freeze ({}); chunks will stay loaded until the world is released.",
                     t.toString()
                  );
                  return;
               }

               if (!chunksmith$announced) {
                  chunksmith$announced = true;
                  chunksmith$LOGGER.info(
                     "Chunksmith: expiring chunk tickets during the world-enter freeze so finished chunks can unload ({} resident, cap {}).",
                     resident,
                     FrozenTicketPurge.effectiveCap(Runtime.getRuntime().maxMemory())
                  );
               }
            }
         } else {
            this.chunksmith$policy.reset();
         }
      }
   }

   @Unique
   private void chunksmith$purge() {
      ((DistanceManagerPurgeInvoker)this.distanceManager).chunksmith$invokePurgeStaleTickets();
   }
}
