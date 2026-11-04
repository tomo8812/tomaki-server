package net.caffeinemc.mods.lithium.common.world.interests;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;

public interface PointOfInterestSetExtended {
   void lithium$collectMatchingPoints(Predicate<Holder<PoiType>> var1, Occupancy var2, Consumer<PoiRecord> var3);
}
