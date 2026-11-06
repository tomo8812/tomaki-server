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
   private void compat$replay(ServerLevel var1, CallbackInfo var2) {
      Compat.replay(var1, (VillageSpawnQueue)this);
   }

   @Redirect(method = "processMainQueue", at = @At(value = "INVOKE", target = "Ljava/util/Queue;poll()Ljava/lang/Object;"))
   private Object compat$poll(
      Queue<BlockPos> var1, ServerLevel var2, VillageSavedData var3, VillageManager var4, boolean var5, boolean var6, boolean var7, long var8
   ) {
      for (int var10 = 0; var10 < 10; var10++) {
         BlockPos var11 = (BlockPos)var1.poll();
         if (var11 == null || Compat.beginCandidate(var2, var11)) {
            return var11;
         }
      }

      return null;
   }

   @Redirect(method = "processLBQueue", at = @At(value = "INVOKE", target = "Ljava/util/Deque;pollFirst()Ljava/lang/Object;"))
   private Object compat$pollLone(Deque<Object> var1, ServerLevel var2, VillageSavedData var3, VillageManager var4, boolean var5, long var6) {
      for (int var8 = 0; var8 < 20; var8++) {
         Object var9 = var1.pollFirst();
         if (var9 == null || Compat.beginCandidate(var2, ((LoneCandidateAccessor)var9).compat$pos())) {
            return var9;
         }
      }

      return null;
   }

   @Redirect(method = "processMainQueue", at = @At(value = "INVOKE", target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
   private void compat$addLoneFromMain(
      Deque<Object> var1, Object var2, ServerLevel var3, VillageSavedData var4, VillageManager var5, boolean var6, boolean var7, boolean var8, long var9
   ) {
      Compat.waiting(var3, ((LoneCandidateAccessor)var2).compat$pos(), true);
      var1.addLast(var2);
   }

   @Redirect(method = "processLBQueue", at = @At(value = "INVOKE", target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
   private void compat$addLone(Deque<Object> var1, Object var2, ServerLevel var3, VillageSavedData var4, VillageManager var5, boolean var6, long var7) {
      Compat.waiting(var3, ((LoneCandidateAccessor)var2).compat$pos(), true);
      var1.addLast(var2);
   }

   @Inject(method = "trySpawnVillage", at = @At("RETURN"))
   private void compat$deferred(
      ServerLevel var1, BlockPos var2, double var3, VillageManager var5, boolean var6, boolean var7, boolean var8, CallbackInfoReturnable<?> var9
   ) {
      if ("DEFERRED".equals(String.valueOf(var9.getReturnValue()))) {
         ReplayController.deferred(var1, var2, !var6);
      }
   }

   @Redirect(
      method = "trySpawnVillage",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;hasChunksAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z")
   )
   private boolean compat$coverage(ServerLevel var1, BlockPos var2, BlockPos var3) {
      return ReplayController.coverageCheck(var1, var2, var3);
   }
}
