package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_5573;
import net.minecraft.class_5582;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;

@Mixin(class_5582.class)
public class MixinTransientEntitySectionManager implements OfLevel {
   @Shadow
   @Final
   class_5573<class_1297> field_27282;
   @Unique
   private class_1937 level;

   @Override
   public class_1937 getLevel() {
      return this.level;
   }

   @Override
   public void setLevel(class_1937 level) {
      this.level = level;
      ((OfLevel)this.field_27282).setLevel(level);
   }
}
