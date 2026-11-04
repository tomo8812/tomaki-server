package net.caffeinemc.mods.lithium.mixin.math.fast_util;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AABB.class)
public class AABBMixin {
   @Shadow
   @Final
   public double minX;
   @Shadow
   @Final
   public double minY;
   @Shadow
   @Final
   public double minZ;
   @Shadow
   @Final
   public double maxX;
   @Shadow
   @Final
   public double maxY;
   @Shadow
   @Final
   public double maxZ;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AABBMixin.class.desiredAssertionStatus();

   // $VF: Unable to simplify switch-on-enum, as the enum class was not able to be found.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Overwrite
   public double min(Axis axis) {
      switch (axis.ordinal()) {
         case 0:
            return this.minX;
         case 1:
            return this.minY;
         case 2:
            return this.minZ;
         default:
            throw new IllegalArgumentException();
      }
   }

   // $VF: Unable to simplify switch-on-enum, as the enum class was not able to be found.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Overwrite
   public double max(Axis axis) {
      switch (axis.ordinal()) {
         case 0:
            return this.maxX;
         case 1:
            return this.maxY;
         case 2:
            return this.maxZ;
         default:
            throw new IllegalArgumentException();
      }
   }

   static {
      assert Axis.X.ordinal() == 0;
      assert Axis.Y.ordinal() == 1;
      assert Axis.Z.ordinal() == 2;
      assert Axis.values().length == 3;
   }
}
