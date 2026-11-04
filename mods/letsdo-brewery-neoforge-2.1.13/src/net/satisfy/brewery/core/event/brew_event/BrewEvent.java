package net.satisfy.brewery.core.event.brew_event;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.satisfy.brewery.core.block.entity.BrewstationBlockEntity;

public abstract class BrewEvent {
   private int timeLeft;

   protected BrewEvent() {
      this(0);
   }

   protected BrewEvent(int time) {
      this.timeLeft = time;
   }

   public void tick(BrewstationBlockEntity entity) {
      this.onTick(entity);
      this.timeLeft--;
   }

   public int getTimeLeft() {
      return this.timeLeft;
   }

   public void setTimeForEvent(int timeLeft) {
      this.timeLeft = timeLeft;
   }

   public abstract CompoundTag save(CompoundTag var1);

   public abstract void load(CompoundTag var1);

   public void onTick(BrewstationBlockEntity entity) {
   }

   public abstract void start(Set<BlockPos> var1, Level var2);

   public abstract boolean isFinish(Set<BlockPos> var1, Level var2);

   public void finish(Set<BlockPos> components, Level level) {
   }
}
