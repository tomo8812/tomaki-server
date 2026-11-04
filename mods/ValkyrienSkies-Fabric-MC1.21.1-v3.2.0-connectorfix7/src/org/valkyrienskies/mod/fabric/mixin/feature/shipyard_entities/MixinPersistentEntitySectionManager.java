package org.valkyrienskies.mod.fabric.mixin.feature.shipyard_entities;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_5573;
import net.minecraft.class_5579;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;

@Mixin(class_5579.class)
public abstract class MixinPersistentEntitySectionManager implements OfLevel {
   @Shadow
   @Final
   class_5573<class_1297> field_27265;
   @Unique
   private class_1937 valkyrienskies$level;

   @Override
   public class_1937 getLevel() {
      return this.valkyrienskies$level;
   }

   @Override
   public void setLevel(class_1937 level) {
      this.valkyrienskies$level = level;
      ((OfLevel)this.field_27265).setLevel(level);
   }
}
