package maki.airship;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;

public final class MountedCameraRotation {
   public static Quaternionf worldRotation(Quaterniondc var0, float var1, float var2, boolean var3) {
      if (var3) {
         var1 += 180.0F;
         var2 = -var2;
      }

      Quaterniond var4 = new Quaterniond().rotationYXZ(Math.PI - Math.toRadians(var1), -Math.toRadians(var2), 0.0);
      return new Quaternionf(var0.mul(var4, new Quaterniond()));
   }
}
