package org.valkyrienskies.mod.mixin.mod_compat.sableCompanion;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.impl.DefaultSableCompanion;
import net.minecraft.class_1937;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(value = DefaultSableCompanion.class, remap = false)
public abstract class MixinDefaultSableCompanion implements SableCompanion {
   public Vector3d projectOutOfSubLevel(class_1937 level, Vector3dc pos, Vector3d dest) {
      Ship ship = VSGameUtilsKt.getLoadedShipManagingPos(level, pos);
      return ship != null ? ship.getTransform().getShipToWorld().transformPosition(pos, dest) : dest.set(pos);
   }

   public class_243 projectOutOfSubLevel(class_1937 level, class_243 pos) {
      return VectorConversionsMCKt.toMinecraft(this.projectOutOfSubLevel(level, VectorConversionsMCKt.toJOML(pos), new Vector3d()));
   }

   public class_243 projectOutOfSubLevel(class_1937 level, class_2374 pos) {
      return super.projectOutOfSubLevel(level, pos);
   }

   public double distanceSquaredWithSubLevels(class_1937 level, Vector3dc a, Vector3dc b) {
      return this.distanceSquaredWithSubLevels(level, a.x(), a.y(), a.z(), b.x(), b.y(), b.z());
   }

   public double distanceSquaredWithSubLevels(class_1937 level, class_2374 a, class_2374 b) {
      return this.distanceSquaredWithSubLevels(
         level, a.method_10216(), a.method_10214(), a.method_10215(), b.method_10216(), b.method_10214(), b.method_10215()
      );
   }

   public double distanceSquaredWithSubLevels(class_1937 level, Vector3dc a, double bX, double bY, double bZ) {
      return this.distanceSquaredWithSubLevels(level, a.x(), a.y(), a.z(), bX, bY, bZ);
   }

   public double distanceSquaredWithSubLevels(class_1937 level, class_2374 a, double bX, double bY, double bZ) {
      return this.distanceSquaredWithSubLevels(level, a.method_10216(), a.method_10214(), a.method_10215(), bX, bY, bZ);
   }

   public double distanceSquaredWithSubLevels(class_1937 level, double aX, double aY, double aZ, double bX, double bY, double bZ) {
      return VSGameUtilsKt.squaredDistanceBetweenInclShips(level, aX, aY, aZ, bX, bY, bZ);
   }

   public Vector3d getVelocity(class_1937 level, Vector3dc pos, Vector3d dest) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
      return ship == null ? dest.zero() : dest.set(ship.getVelocity());
   }

   public class_243 getVelocity(class_1937 level, class_243 pos) {
      return VectorConversionsMCKt.toMinecraft(this.getVelocity(level, VectorConversionsMCKt.toJOML(pos), new Vector3d()));
   }

   public boolean isInPlotGrid(class_1937 level, int chunkX, int chunkZ) {
      return VSGameUtilsKt.isChunkInShipyard(level, chunkX, chunkZ);
   }
}
