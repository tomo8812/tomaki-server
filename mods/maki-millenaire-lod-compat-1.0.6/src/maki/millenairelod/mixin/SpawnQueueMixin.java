package maki.millenairelod.mixin;

import java.util.Deque;
import java.util.Queue;
import maki.millenairelod.Compat;
import maki.millenairelod.ReplayController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.village.VillageManager;
import org.millenaire.village.VillageSavedData;
import org.millenaire.world.VillageSpawnQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VillageSpawnQueue.class, remap = false)
public abstract class SpawnQueueMixin {
   @Inject(method = "trySpawnNext", at = @At("HEAD"))
   private void compat$replay(ServerLevel level, CallbackInfo ci) {
      Compat.replay(level, (VillageSpawnQueue)this);
   }

   @Redirect(method = "processMainQueue", at = @At(value = "INVOKE", target = "Ljava/util/Queue;poll()Ljava/lang/Object;"))
   private Object compat$poll(
      Queue<BlockPos> queue, ServerLevel level, VillageSavedData saved, VillageManager manager, boolean villages, boolean lone, boolean log, long tick
   ) {
      for (int i = 0; i < 10; i++) {
         BlockPos pos = queue.poll();
         if (pos == null || Compat.beginCandidate(level, pos)) {
            return pos;
         }
      }

      return null;
   }

   @Redirect(method = "processLBQueue", at = @At(value = "INVOKE", target = "Ljava/util/Deque;pollFirst()Ljava/lang/Object;"))
   private Object compat$pollLone(Deque<Object> queue, ServerLevel level, VillageSavedData saved, VillageManager manager, boolean log, long tick) {
      for (int i = 0; i < 20; i++) {
         Object candidate = queue.pollFirst();
         if (candidate == null || Compat.beginCandidate(level, ((LoneCandidateAccessor)candidate).compat$pos())) {
            return candidate;
         }
      }

      return null;
   }

   @Redirect(method = "processMainQueue", at = @At(value = "INVOKE", target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
   private void compat$addLoneFromMain(
      Deque<Object> queue,
      Object candidate,
      ServerLevel level,
      VillageSavedData saved,
      VillageManager manager,
      boolean villages,
      boolean lone,
      boolean log,
      long tick
   ) {
      Compat.waiting(level, ((LoneCandidateAccessor)candidate).compat$pos(), true);
      queue.addLast(candidate);
   }

   @Redirect(method = "processLBQueue", at = @At(value = "INVOKE", target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
   private void compat$addLone(Deque<Object> queue, Object candidate, ServerLevel level, VillageSavedData saved, VillageManager manager, boolean log, long tick) {
      Compat.waiting(level, ((LoneCandidateAccessor)candidate).compat$pos(), true);
      queue.addLast(candidate);
   }

   @Redirect(
      method = "trySpawnVillage",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;hasChunksAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z")
   )
   private boolean compat$coverage(ServerLevel level, BlockPos from, BlockPos to) {
      return ReplayController.coverageCheck(level, from, to);
   }

   @Inject(method = "trySpawnVillage", at = @At("RETURN"))
   private void compat$deferred(
      ServerLevel level, BlockPos pos, double distance, VillageManager manager, boolean lone, boolean log, boolean keyOnly, CallbackInfoReturnable<?> cir
   ) {
      if ("DEFERRED".equals(String.valueOf(cir.getReturnValue()))) {
         ReplayController.deferred(level, pos, !lone);
      }
   }
}
