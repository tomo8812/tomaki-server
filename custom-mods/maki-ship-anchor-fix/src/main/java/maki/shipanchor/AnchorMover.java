package maki.shipanchor;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3dc;

public final class AnchorMover {

   private AnchorMover() {
   }

   public static boolean move(Entity var0, Vector3dc var1) {
      if (var0 != null && var1 != null && var1.isFinite()) {
         double var2 = var1.x() - var0.getX();
         double var4 = var1.y() - var0.getY();
         double var6 = var1.z() - var0.getZ();
         if (Double.isFinite(var2) && Double.isFinite(var4) && Double.isFinite(var6)) {
            if (var2 * var2 + var4 * var4 + var6 * var6 <= 1.0E-12) {
               return false;
            }

            var0.setPos(var1.x(), var1.y(), var1.z());
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
