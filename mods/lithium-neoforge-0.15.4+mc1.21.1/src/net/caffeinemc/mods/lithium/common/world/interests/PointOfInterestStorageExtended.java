package net.caffeinemc.mods.lithium.common.world.interests;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.border.WorldBorder;

public interface PointOfInterestStorageExtended {
   Optional<PoiRecord> lithium$findNearestForPortalLogic(
      BlockPos var1, int var2, Holder<PoiType> var3, Occupancy var4, Predicate<PoiRecord> var5, WorldBorder var6
   );
}
