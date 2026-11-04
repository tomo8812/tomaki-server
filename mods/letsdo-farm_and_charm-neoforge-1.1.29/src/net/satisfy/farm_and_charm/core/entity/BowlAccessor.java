package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;

public interface BowlAccessor {
   interface FedTracker {
      void farmAndCharm$$markAsFed();

      boolean farmAndCharm$$isFed();

      void farmAndCharm$$resetFed();
   }

   interface StayNearBowl {
      void farmAndCharm$setStayCenter(BlockPos var1);

      void farmAndCharm$clearStayRestriction();

      boolean farmAndCharm$hasStayRestriction();

      boolean farmAndCharm$isWithinStayRange(BlockPos var1);

      BlockPos farmAndCharm$getStayCenter();
   }
}
