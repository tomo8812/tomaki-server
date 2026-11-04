package net.caffeinemc.mods.lithium.mixin.math.fast_util;

import net.minecraft.core.Direction.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

public class AxisCycleDirectionMixin {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !AxisCycleDirectionMixin.class.desiredAssertionStatus();

   static {
      assert Axis.X.ordinal() == 0;
      assert Axis.Y.ordinal() == 1;
      assert Axis.Z.ordinal() == 2;
      assert Axis.values().length == 3;
   }

   @Mixin(targets = "net/minecraft/core/AxisCycle$3")
   public static class BackwardMixin {
      // $VF: Unable to simplify switch-on-enum, as the enum class was not able to be found.
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Overwrite
      public Axis cycle(Axis axis) {
         switch (axis.ordinal()) {
            case 0:
               return Axis.Z;
            case 1:
               return Axis.X;
            case 2:
               return Axis.Y;
            default:
               throw new IllegalArgumentException();
         }
      }
   }

   @Mixin(targets = "net/minecraft/core/AxisCycle$2")
   public static class ForwardMixin {
      // $VF: Unable to simplify switch-on-enum, as the enum class was not able to be found.
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Overwrite
      public Axis cycle(Axis axis) {
         switch (axis.ordinal()) {
            case 0:
               return Axis.Y;
            case 1:
               return Axis.Z;
            case 2:
               return Axis.X;
            default:
               throw new IllegalArgumentException();
         }
      }
   }
}
