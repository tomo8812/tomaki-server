package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.bodies.properties.BodyTransformImpl;

public final class Ew {
   public static final ShipTransform a(Vector3dc var0, Vector3dc var1, Quaterniondc var2, Vector3dc var3) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      return new BodyTransformImpl(var0, var2, var3, var1);
   }
}
