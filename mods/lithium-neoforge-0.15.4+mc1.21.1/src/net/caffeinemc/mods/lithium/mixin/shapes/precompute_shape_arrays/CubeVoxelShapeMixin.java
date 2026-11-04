package net.caffeinemc.mods.lithium.mixin.shapes.precompute_shape_arrays;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CubeVoxelShape.class)
public class CubeVoxelShapeMixin {
   private static final Axis[] AXIS = Axis.values();
   private DoubleList[] list;

   @Inject(method = "<init>(Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;)V", at = @At("RETURN"))
   private void onConstructed(DiscreteVoxelShape voxels, CallbackInfo ci) {
      this.list = new DoubleList[AXIS.length];

      for (Axis axis : AXIS) {
         this.list[axis.ordinal()] = new CubePointRange(voxels.getSize(axis));
      }
   }

   @Overwrite
   public DoubleList getCoords(Axis axis) {
      return this.list[axis.ordinal()];
   }
}
