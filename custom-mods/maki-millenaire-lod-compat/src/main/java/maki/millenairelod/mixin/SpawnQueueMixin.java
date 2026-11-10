package maki.millenairelod.mixin;

import java.util.Deque;
import java.util.Queue;
import maki.millenairelod.Compat;
import maki.millenairelod.ReplayController;
import maki.millenairelod.VillagePlacementReadiness;
import net.minecraft.network.chat.Component;
import org.millenaire.culture.VillageType;
import org.millenaire.world.VillageSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.village.VillageManager;
import org.millenaire.village.VillageSavedData;
import org.millenaire.world.VillageSpawnQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VillageSpawnQueue.class, remap = false)
public abstract class SpawnQueueMixin {
    @Unique private boolean maki$awaitingPersistentArea;

    @Inject(method = "trySpawnVillage", at = @At("HEAD"))
    private void maki$resetLegacyAdmission(ServerLevel level, BlockPos pos, double distance,
                                          VillageManager manager, boolean lone, boolean log,
                                          boolean keyOnly, CallbackInfoReturnable<?> cir) {
        maki$awaitingPersistentArea = false;
    }

    // The legacy village/lone-building path bypasses SiteHunter entirely.
    // Guard the *actual* chosen type's footprint at the final construction
    // callsite. A non-null Component makes the native code return without
    // mutating the world; the RETURN injector maps this guard to DEFERRED so
    // Millenaire retains its candidate instead of recording a failed site.
    @Redirect(
        method = "trySpawnVillage",
        at = @At(value = "INVOKE",
            target = "Lorg/millenaire/world/VillageSpawner;spawnVillage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lorg/millenaire/culture/VillageType;I)Lnet/minecraft/network/chat/Component;"),
        require = 1
    )
    private Component maki$guardLegacyConstruction(ServerLevel level, BlockPos center,
                                                   VillageType type, int completion) {
        if (!VillagePlacementReadiness.areaReady(level, center.getX(), center.getZ(), type.radius())) {
            maki$awaitingPersistentArea = true;
            return Component.literal("waiting for loaded, normally persistent FULL village area");
        }
        return VillageSpawner.spawnVillage(level, center, type, completion);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Unique
    private static Object maki$deferredResult() {
        try {
            Class<?> type = Class.forName("org.millenaire.world.VillageSpawnQueue$SpawnResult",
                    false, VillageSpawnQueue.class.getClassLoader());
            return Enum.valueOf((Class) type.asSubclass(Enum.class), "DEFERRED");
        } catch (ClassNotFoundException | IllegalArgumentException error) {
            throw new IllegalStateException("Millenaire 9.1.0 DEFERRED result is unavailable", error);
        }
    }

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
    @Inject(method = "trySpawnVillage", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void compat$deferred(ServerLevel level, BlockPos pos, double distance, VillageManager manager,
                                  boolean lone, boolean log, boolean keyOnly, CallbackInfoReturnable<?> cir) {
        if (maki$awaitingPersistentArea) {
            maki$awaitingPersistentArea = false;
            ((CallbackInfoReturnable) cir).setReturnValue(maki$deferredResult());
        }
        if ("DEFERRED".equals(String.valueOf(cir.getReturnValue()))) {
            ReplayController.deferred(level, pos, !lone);
        }
    }
}
