package maki.idlepregen.mixin;

import java.util.List;
import maki.idlepregen.MillenaireInitTelemetry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.building.BuildingInstance;
import org.millenaire.village.Village;
import org.millenaire.village.VillageWaypointGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VillageWaypointGraph.class, remap = false)
public abstract class VillageWaypointGraphMixin {
    @Inject(
            method = "rebuild(Ljava/util/List;Lnet/minecraft/core/BlockPos;Lnet/minecraft/server/level/ServerLevel;Lorg/millenaire/village/Village;)V",
            at = @At("HEAD"))
    private void maki$beginGraphMeasurement(
            List<BuildingInstance> buildings,
            BlockPos villageCenter,
            ServerLevel level,
            Village village,
            CallbackInfo ci) {
        MillenaireInitTelemetry.beginGraph(village);
    }

    @Inject(
            method = "rebuild(Ljava/util/List;Lnet/minecraft/core/BlockPos;Lnet/minecraft/server/level/ServerLevel;Lorg/millenaire/village/Village;)V",
            at = @At("RETURN"))
    private void maki$endGraphMeasurement(
            List<BuildingInstance> buildings,
            BlockPos villageCenter,
            ServerLevel level,
            Village village,
            CallbackInfo ci) {
        MillenaireInitTelemetry.endGraph();
    }
}
