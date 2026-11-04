package org.valkyrienskies.mod.mixin.mod_compat.bluemap;

import com.flowpowered.math.vector.Vector3i;
import de.bluecolored.bluemap.core.map.TileMetaConsumer;
import de.bluecolored.bluemap.core.map.hires.HiresModelManager;
import de.bluecolored.bluemap.core.map.hires.HiresModelRenderer;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.world.World;
import net.minecraft.class_1937;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.mixinducks.mod_compat.bluemap.WorldDuck;

@Pseudo
@Mixin(HiresModelManager.class)
public class MixinHiresModelManager {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MixinHiresModelManager.class.desiredAssertionStatus();

   @Redirect(
      remap = false,
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lde/bluecolored/bluemap/core/map/hires/HiresModelRenderer;render(Lde/bluecolored/bluemap/core/world/World;Lcom/flowpowered/math/vector/Vector3i;Lcom/flowpowered/math/vector/Vector3i;Lde/bluecolored/bluemap/core/map/hires/TileModel;Lde/bluecolored/bluemap/core/map/TileMetaConsumer;)V"
      )
   )
   void renderModel(HiresModelRenderer instance, World world, Vector3i min, Vector3i max, TileModel model, TileMetaConsumer tmc) {
      AABBd aabb = new AABBd(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
      class_1937 level = ((WorldDuck)world).valkyrienskies$getCorrelatingLevel();
      if (level == null) {
         System.out.println("Valkyrien Skies x BlueMap: Could not find correlating level for bluemap world");
      } else {
         Iterable<Ship> ships = VSGameUtilsKt.getShipsIntersecting(level, aabb);
         int start = model.size();

         for (Ship ship : ships) {
            if (aabb.containsPoint(ship.getTransform().getPositionInWorld())) {
               AABBic shipAABB = ship.getShipAABB();
               assert shipAABB != null;
               Vector3i shipMin = new Vector3i(shipAABB.minX() - 1, shipAABB.minY() - 1, shipAABB.minZ() - 1);
               Vector3i shipMax = new Vector3i(shipAABB.maxX() + 1, shipAABB.maxY() + 1, shipAABB.maxZ() + 1);
               instance.render(world, shipMin, shipMax, model, tmc);
               Vector3d preTranslation = new Vector3d(shipMin.getX(), 0.0, shipMin.getZ());
               Vector3d postTranslation = new Vector3d(-min.getX(), 0.0, -min.getZ());
               this.valkyrienskies$transformModel(start, model.size(), model, preTranslation, postTranslation, ship.getTransform().getShipToWorld());
               start = model.size();
            }
         }

         instance.render(world, min, max, model, tmc);
      }
   }

   @Unique
   private void valkyrienskies$transformModel(int start, int end, TileModel model, Vector3dc preTranslation, Vector3dc postTranslation, Matrix4dc transform) {
      float[] positions = ((HiresTileModelAccessor)model).getPositions();

      for (int face = start; face < end; face++) {
         for (int i = 0; i < 3; i++) {
            int index = face * 9 + i * 3;
            double x = positions[index] + preTranslation.x();
            double y = positions[index + 1] + preTranslation.y();
            double z = positions[index + 2] + preTranslation.z();
            double newX = x * transform.m00() + y * transform.m10() + z * transform.m20() + transform.m30() + postTranslation.x();
            double newY = x * transform.m01() + y * transform.m11() + z * transform.m21() + transform.m31() + postTranslation.y();
            double newZ = x * transform.m02() + y * transform.m12() + z * transform.m22() + transform.m32() + postTranslation.z();
            positions[index] = (float)newX;
            positions[index + 1] = (float)newY;
            positions[index + 2] = (float)newZ;
         }
      }
   }
}
