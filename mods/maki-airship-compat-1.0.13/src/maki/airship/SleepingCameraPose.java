package maki.airship;

import net.minecraft.core.Direction;

public final class SleepingCameraPose {
   public static float localYaw(Direction var0) {
      return var0 == null ? 0.0F : var0.toYRot() - 180.0F;
   }
}
