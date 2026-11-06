package maki.airship;

import net.minecraft.core.Direction;

public final class SleepingCameraPose {
    private SleepingCameraPose() {}

    public static float localYaw(Direction bedDirection) {
        return bedDirection == null ? 0.0F : bedDirection.toYRot() - 180.0F;
    }
}
