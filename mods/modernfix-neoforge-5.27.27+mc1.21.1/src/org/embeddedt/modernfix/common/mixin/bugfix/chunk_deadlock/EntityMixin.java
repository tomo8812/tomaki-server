package org.embeddedt.modernfix.common.mixin.bugfix.chunk_deadlock;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.gameevent.GameEvent;
import org.embeddedt.modernfix.ModernFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
   @WrapWithCondition(
      method = "addPassenger",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;)V")
   )
   private boolean onlyAddIfSelfChunkLoaded(Entity instance, Holder<GameEvent> gameEvent, Entity entity) {
      ChunkPos chunkPos = instance.chunkPosition();
      if (instance.level() instanceof ServerLevel serverLevel && serverLevel.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z) == null) {
         ModernFix.LOGGER.warn("Skipped emitting ENTITY_MOUNT game event for entity {} as it would cause deadlock", instance.toString());
         return false;
      } else {
         return true;
      }
   }
}
