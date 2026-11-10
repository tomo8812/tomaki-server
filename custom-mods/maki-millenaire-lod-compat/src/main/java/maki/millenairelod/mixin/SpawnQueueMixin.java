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
        Compat.replay(level, (VillageSpawnQueue)(Object)this);
    }

    // Millenaire 9.1.0's default SiteHunter path drains the main candidate queue
    // directly from trySpawnNext, bypassing processMainQueue. Apply the same
    // loaded-FULL and retry checks before handing a candidate to SiteHunter.
    @Redirect(method = "trySpawnNext",
        at = @At(value = "INVOKE", target = "Ljava/util/Queue;poll()Ljava/lang/Object;"),
        require = 1)
    private Object compat$pollForSiteHunter(Queue<BlockPos> queue, ServerLevel level) {
        for (int i = 0; i < 10; i++) {
            BlockPos pos = queue.poll();
            if (pos == null || Compat.beginCandidate(level, pos)) return pos;
        }
        return null;
    }

    @Redirect(method = "processMainQueue",
        at = @At(value = "INVOKE", target = "Ljava/util/Queue;poll()Ljava/lang/Object;"))
    private Object compat$poll(Queue<BlockPos> queue, ServerLevel level, VillageSavedData saved,
                               VillageManager manager, boolean villages, boolean lone, boolean log, long tick) {
        for (int i = 0; i < 10; i++) {
            BlockPos pos = queue.poll();
            if (pos == null || Compat.beginCandidate(level, pos)) return pos;
        }
        return null;
    }

    @Redirect(method = "processLBQueue",
        at = @At(value = "INVOKE", target = "Ljava/util/Deque;pollFirst()Ljava/lang/Object;"))
    private Object compat$pollLone(Deque<Object> queue, ServerLevel level, VillageSavedData saved,
                                   VillageManager manager, boolean log, long tick) {
        // Unlike the main queue, this deque carries its own retry/TTL state.
        // Preserve blocked LBCandidates in place; replaying them through the
        // main queue would silently turn a lone-building retry into a village.
        int scans = Math.min(20, queue.size());
        for (int i = 0; i < scans; i++) {
            Object candidate = queue.pollFirst();
            if (candidate == null) return null;
            BlockPos pos = ((LoneCandidateAccessor)candidate).compat$pos();
            if (Compat.beginCandidate(level, pos)) return candidate;
            Compat.waiting(level, pos, true);
            queue.addLast(candidate);
        }
        return null;
    }

    @Redirect(method = "processMainQueue",
        at = @At(value = "INVOKE", target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
    private void compat$addLoneFromMain(Deque<Object> queue, Object candidate, ServerLevel level,
                                       VillageSavedData saved, VillageManager manager,
                                       boolean villages, boolean lone, boolean log, long tick) {
        Compat.waiting(level, ((LoneCandidateAccessor)candidate).compat$pos(), true);
        queue.addLast(candidate);
    }

    @Redirect(method = "processLBQueue",
        at = @At(value = "INVOKE", target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
    private void compat$addLone(Deque<Object> queue, Object candidate, ServerLevel level,
                               VillageSavedData saved, VillageManager manager, boolean log, long tick) {
        Compat.waiting(level, ((LoneCandidateAccessor)candidate).compat$pos(), true);
        queue.addLast(candidate);
    }

    // 9.1.0 moved lone-building coverage into processLBQueue.isCoverageComplete.
    // Do not redirect a call to ServerLevel.hasChunksAt inside trySpawnVillage:
    // that call no longer exists, and the native resolved-chunk check must run.
    @Inject(method = "trySpawnVillage", at = @At("RETURN"))
    private void compat$deferred(ServerLevel level, BlockPos pos, double distance, VillageManager manager,
                                  boolean lone, boolean log, boolean keyOnly, CallbackInfoReturnable<?> cir) {
        if ("DEFERRED".equals(String.valueOf(cir.getReturnValue()))) {
            ReplayController.deferred(level, pos, !lone);
        }
    }
}
