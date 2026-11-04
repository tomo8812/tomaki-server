package org.valkyrienskies.mod.mixin.feature.world_border;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import javax.annotation.Nullable;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2784;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;

@Mixin(class_2784.class)
public class MixinWorldBorder implements OfLevel {
   @Unique
   @Nullable
   private class_1937 level;

   @ModifyReturnValue(method = "isWithinBounds(Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"))
   public boolean shipsWithinBounds(boolean isWithinBounds, class_2338 pos) {
      return isWithinBounds || this.level != null && VSGameUtilsKt.getShipManagingPos(this.level, pos) != null;
   }

   @ModifyReturnValue(method = "isWithinBounds(Lnet/minecraft/world/level/ChunkPos;)Z", at = @At("RETURN"))
   public boolean shipsWithinBounds(boolean isWithinBounds, class_1923 pos) {
      return isWithinBounds || this.level != null && VSGameUtilsKt.getShipManagingPos(this.level, pos) != null;
   }

   @ModifyReturnValue(method = "isWithinBounds(DD)Z", at = @At("RETURN"))
   public boolean shipsWithinBounds(boolean isWithinBounds, double x, double z) {
      return isWithinBounds || this.level != null && VSGameUtilsKt.getShipManagingPos(this.level, (int)x >> 4, (int)z >> 4) != null;
   }

   @ModifyReturnValue(method = "isWithinBounds(DDD)Z", at = @At("RETURN"))
   public boolean shipsWithinBounds(boolean isWithinBounds, double x, double z, double offset) {
      return isWithinBounds || this.level != null && VSGameUtilsKt.getShipManagingPos(this.level, (int)x >> 4, (int)z >> 4) != null;
   }

   @ModifyReturnValue(method = "isWithinBounds(Lnet/minecraft/world/phys/AABB;)Z", at = @At("RETURN"))
   public boolean shipsWithinBounds(boolean isWithinBounds, class_238 aabb) {
      if (!isWithinBounds && this.level != null) {
         Ship s1 = VSGameUtilsKt.getShipManagingPos(this.level, (int)aabb.field_1323 >> 4, (int)aabb.field_1321 >> 4);
         Ship s2 = VSGameUtilsKt.getShipManagingPos(this.level, (int)aabb.field_1320 >> 4, (int)aabb.field_1324 >> 4);
         return s1 != null && s2 != null && s1.getId() == s2.getId();
      } else {
         return isWithinBounds;
      }
   }

   @Nullable
   @Override
   public class_1937 getLevel() {
      return this.level;
   }

   @Override
   public void setLevel(@Nullable class_1937 level) {
      this.level = level;
   }
}
