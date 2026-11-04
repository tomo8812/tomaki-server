package net.caffeinemc.mods.lithium.mixin.ai.task.replace_streams;

import java.util.Set;
import net.caffeinemc.mods.lithium.common.ai.WeightedListIterable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GateBehavior.class)
public abstract class GateBehaviorMixin<E extends LivingEntity> implements BehaviorControl<E> {
   @Shadow
   @Final
   private ShufflingList<BehaviorControl<? super E>> behaviors;
   @Shadow
   @Final
   private Set<MemoryModuleType<?>> exitErasedMemories;
   @Shadow
   private Status status;

   @Overwrite
   public final void tickOrStop(ServerLevel world, E entity, long time) {
      boolean hasOneTaskRunning = false;

      for (BehaviorControl<? super E> task : WeightedListIterable.cast(this.behaviors)) {
         if (task.getStatus() == Status.RUNNING) {
            task.tickOrStop(world, entity, time);
            hasOneTaskRunning |= task.getStatus() == Status.RUNNING;
         }
      }

      if (!hasOneTaskRunning) {
         this.doStop(world, entity, time);
      }
   }

   @Overwrite
   public final void doStop(ServerLevel world, E entity, long time) {
      this.status = Status.STOPPED;

      for (BehaviorControl<? super E> task : WeightedListIterable.cast(this.behaviors)) {
         if (task.getStatus() == Status.RUNNING) {
            task.doStop(world, entity, time);
         }
      }

      Brain<?> brain = entity.getBrain();

      for (MemoryModuleType<?> module : this.exitErasedMemories) {
         brain.eraseMemory(module);
      }
   }
}
