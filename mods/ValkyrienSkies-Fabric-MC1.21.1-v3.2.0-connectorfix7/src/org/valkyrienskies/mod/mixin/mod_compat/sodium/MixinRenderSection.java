package org.valkyrienskies.mod.mixin.mod_compat.sodium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(value = RenderSection.class, remap = false)
public abstract class MixinRenderSection {
   @Shadow
   public abstract int getCenterX();

   @Shadow
   public abstract int getCenterY();

   @Shadow
   public abstract int getCenterZ();

   @WrapMethod(method = "getSquaredDistance(FFF)F")
   private float squareDistanceIfInShipyard(float x, float y, float z, Operation<Float> original) {
      class_638 world = class_310.method_1551().field_1687;
      ClientShip shipObject = VSGameUtilsKt.getLoadedShipManagingPos(world, this.getCenterX(), this.getCenterY(), this.getCenterZ());
      if (shipObject != null) {
         Vector3dc chunkPosInWorld = shipObject.getRenderTransform()
            .getShipToWorld()
            .transformPosition(new Vector3d(this.getCenterX(), this.getCenterY(), this.getCenterZ()));
         return (float)chunkPosInWorld.distanceSquared(x, y, z);
      } else {
         return (Float)original.call(new Object[]{x, y, z});
      }
   }
}
