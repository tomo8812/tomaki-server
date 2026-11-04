package net.caffeinemc.mods.lithium.mixin.ai.poi.fast_portals;

import java.util.Optional;
import net.caffeinemc.mods.lithium.common.util.POIRegistryEntries;
import net.caffeinemc.mods.lithium.common.world.interests.PointOfInterestStorageExtended;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PortalForcer.class)
public class PortalForcerMixin {
   @Shadow
   @Final
   private ServerLevel level;

   @Overwrite
   public Optional<BlockPos> findClosestPortalPosition(BlockPos centerPos, boolean dstIsNether, WorldBorder worldBorder) {
      int searchRadius = dstIsNether ? 16 : 128;
      PoiManager poiStorage = this.level.getPoiManager();
      poiStorage.ensureLoadedAndValid(this.level, centerPos, searchRadius);
      Optional<PoiRecord> ret = ((PointOfInterestStorageExtended)poiStorage)
         .lithium$findNearestForPortalLogic(
            centerPos,
            searchRadius,
            POIRegistryEntries.NETHER_PORTAL_ENTRY,
            Occupancy.ANY,
            poi -> this.level.getBlockState(poi.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS),
            worldBorder
         );
      return ret.map(PoiRecord::getPos);
   }
}
