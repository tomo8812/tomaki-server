package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;
import org.valkyrienskies.core.internal.collision.VsiConvexPolygonc;

public final class Dj {
   public static final Db a(VsiConvexPolygonc var0, Vector3dc var1, Db var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      double var3 = Double.POSITIVE_INFINITY;
      double var5 = Double.NEGATIVE_INFINITY;
      Iterator var14 = var0.getPoints().iterator();

      while (var14.hasNext()) {
         double var9 = ((Vector3dc)var14.next()).dot(var1);
         var3 = Math.min(var3, var9);
         var5 = Math.max(var5, var9);
      }

      double var12 = var3;
      var2.b = var12;
      var12 = var5;
      var2.c = var12;
      return var2;
   }
}
