package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface Dg {
   boolean b();

   Vector3dc c();

   double d();

   default Vector3d a(Vector3d var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      Vector3d var10000 = this.c().mul(this.d(), var1);
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }
}
