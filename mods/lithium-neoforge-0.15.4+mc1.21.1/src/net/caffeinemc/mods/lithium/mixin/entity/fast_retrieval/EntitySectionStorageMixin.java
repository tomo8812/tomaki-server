package net.caffeinemc.mods.lithium.mixin.entity.fast_retrieval;

import net.minecraft.core.SectionPos;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.AbortableIterationConsumer.Continuation;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntitySectionStorage.class)
public abstract class EntitySectionStorageMixin<T extends EntityAccess> {
   @Shadow
   @Nullable
   public abstract EntitySection<T> getSection(long var1);

   @Inject(
      method = "forEachAccessibleNonEmptySection",
      at = @At(value = "INVOKE_ASSIGN", shift = Shift.AFTER, target = "Lnet/minecraft/core/SectionPos;posToSectionCoord(D)I", ordinal = 5),
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   public void forEachInBox(
      AABB box, AbortableIterationConsumer<EntitySection<T>> action, CallbackInfo ci, int i, int minX, int minY, int minZ, int maxX, int maxY, int maxZ
   ) {
      if (maxX < minX + 4 && maxZ < minZ + 4) {
         ci.cancel();

         for (int x = minX; x <= maxX; x++) {
            for (int z = Math.max(minZ, 0); z <= maxZ; z++) {
               if (this.forEachInColumn(x, minY, maxY, z, action).shouldAbort()) {
                  return;
               }
            }

            int bound = Math.min(-1, maxZ);

            for (int z = minZ; z <= bound; z++) {
               if (this.forEachInColumn(x, minY, maxY, z, action).shouldAbort()) {
                  return;
               }
            }
         }
      }
   }

   private Continuation forEachInColumn(int x, int minY, int maxY, int z, AbortableIterationConsumer<EntitySection<T>> action) {
      Continuation ret = Continuation.CONTINUE;

      for (int y = Math.max(minY, 0); y <= maxY; y++) {
         if ((ret = this.consumeSection(SectionPos.asLong(x, y, z), action)).shouldAbort()) {
            return ret;
         }
      }

      int bound = Math.min(-1, maxY);

      for (int y = minY; y <= bound; y++) {
         if ((ret = this.consumeSection(SectionPos.asLong(x, y, z), action)).shouldAbort()) {
            return ret;
         }
      }

      return ret;
   }

   private Continuation consumeSection(long pos, AbortableIterationConsumer<EntitySection<T>> action) {
      EntitySection<T> section = this.getSection(pos);
      return section != null && 0 != section.size() && section.getStatus().isAccessible() ? action.accept(section) : Continuation.CONTINUE;
   }
}
