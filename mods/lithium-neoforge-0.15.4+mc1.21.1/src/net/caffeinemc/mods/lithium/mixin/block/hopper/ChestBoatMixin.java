package net.caffeinemc.mods.lithium.mixin.block.hopper;

import net.caffeinemc.mods.lithium.common.tracking.entity.ToggleableMovementTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ChestBoat.class, priority = 2000)
public abstract class ChestBoatMixin extends Entity {
   public ChestBoatMixin(EntityType<?> type, Level world) {
      super(type, world);
   }

   @Intrinsic
   public void rideTick() {
      this.tickRidingSummarizeMovementNotifications();
   }

   @Unique
   private void tickRidingSummarizeMovementNotifications() {
      EntityInLevelCallback changeListener = ((EntityAccessor)this).getChangeListener();
      if (changeListener instanceof ToggleableMovementTracker toggleableMovementTracker) {
         Vec3 beforeTickPos = this.position();
         int beforeMovementNotificationMask = toggleableMovementTracker.lithium$setNotificationMask(0);
         super.rideTick();
         toggleableMovementTracker.lithium$setNotificationMask(beforeMovementNotificationMask);
         if (!beforeTickPos.equals(this.position())) {
            changeListener.onMove();
         }
      } else {
         super.rideTick();
      }
   }
}
