package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import kotlin.comparisons.ComparisonsKt;
import org.joml.Vector3d;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.internal.collision.VsiConvexPolygonc;
import org.valkyrienskies.core.util.AABBdUtilKt;

public final class Dk$a<T> implements Comparator {
   // $VF: synthetic field
   private AABBdc a;

   public Dk$a(AABBdc var1) {
      this.a = var1;
      super();
   }

   @Override
   public final int compare(T a, T b) {
      VsiConvexPolygonc var3;
      Vector3d var4 = (var3 = (VsiConvexPolygonc)a).computeCenterPos(new Vector3d());
      Comparable var10000 = AABBdUtilKt.signedDistanceTo(this.a, var4);
      VsiConvexPolygonc var5 = (VsiConvexPolygonc)b;
      Comparable var7 = var10000;
      var4 = var5.computeCenterPos(new Vector3d());
      return ComparisonsKt.compareValues(var7, AABBdUtilKt.signedDistanceTo(this.a, var4));
   }
}
