package maki.fndseat;

import org.joml.Matrix4dc;
import org.joml.Vector3d;

public final class FurnitureCoordinates {
   public static Vector3d localPrevious(double var0, double var2, double var4, boolean var6, Matrix4dc var7) {
      Vector3d var8 = new Vector3d(var0, var2, var4);
      return var6 ? var8 : var7.transformPosition(var8);
   }
}
