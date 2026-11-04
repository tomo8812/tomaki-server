package net.caffeinemc.mods.lithium.common.tracking.entity;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;

public interface EntityMovementTrackerSection {
   void lithium$addListener(SectionedEntityMovementTracker<?, ?> var1);

   void lithium$removeListener(EntitySectionStorage<?> var1, SectionedEntityMovementTracker<?, ?> var2);

   void lithium$trackEntityMovement(int var1, long var2);

   long lithium$getChangeTime(int var1);

   <S, E extends EntityAccess> void lithium$listenToMovementOnce(SectionedEntityMovementTracker<E, S> var1, int var2);

   <S, E extends EntityAccess> void lithium$removeListenToMovementOnce(SectionedEntityMovementTracker<E, S> var1, int var2);
}
