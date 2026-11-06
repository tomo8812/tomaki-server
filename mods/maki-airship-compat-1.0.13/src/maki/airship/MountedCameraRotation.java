package maki.airship;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;

public final class MountedCameraRotation {
   private MountedCameraRotation() {
   }

   public static Quaternionf worldRotation(Quaterniondc shipRotation, float yaw, float pitch, boolean reverse) {
      if (reverse) {
         yaw += 180.0F;
         pitch = -pitch;
      }

      Quaterniond local = new Quaterniond().rotationYXZ(Math.PI - Math.toRadians(yaw), -Math.toRadians(pitch), 0.0);
      return new Quaternionf(shipRotation.mul(local, new Quaterniond()));
   }
}
