package org.valkyrienskies.mod.mixin.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.class_3556;
import net.minecraft.class_3560;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_3560.class)
public interface LayerLightSectionStorageAccessor {
   @Accessor("visibleSectionData")
   class_3556 vs$getVisibleSectionData();

   @Accessor("updatingSectionData")
   class_3556 vs$getUpdatingSectionData();

   @Accessor("queuedSections")
   Long2ObjectMap vs$getQueuedSections();
}
