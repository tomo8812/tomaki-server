package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import org.joml.Vector3dc;
import org.valkyrienskies.core.internal.collision.VsiConvexPolygonc;

public interface Di {
   void a(VsiConvexPolygonc var1, VsiConvexPolygonc var2, Iterator<? extends Vector3dc> var3, Dd var4, Db var5, Db var6, Vector3dc var7);

   // $VF: synthetic method
   static void a(Di var0, VsiConvexPolygonc var1, VsiConvexPolygonc var2, Iterator var3, Dd var4, Db var5, Db var6, Vector3dc var7, int var8, Object var9) {
      if (var9 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: checkIfColliding");
      }

      if ((var8 & 64) != 0) {
         var7 = null;
      }

      var0.a(var1, var2, var3, var4, var5, var6, var7);
   }

   Vector3dc a(
      VsiConvexPolygonc var1, Vector3dc var2, VsiConvexPolygonc var3, Iterator<? extends Vector3dc> var4, Db var5, Db var6, double var7, Vector3dc var9
   );

   // $VF: synthetic method
   static Vector3dc a(
      Di var0,
      VsiConvexPolygonc var1,
      Vector3dc var2,
      VsiConvexPolygonc var3,
      Iterator var4,
      Db var5,
      Db var6,
      double var7,
      Vector3dc var9,
      int var10,
      Object var11
   ) {
      return var0.a(var1, var2, var3, var4, var5, var6, var7, null);
   }

   Vector3dc a(VsiConvexPolygonc var1, Vector3dc var2, VsiConvexPolygonc var3, Iterator<? extends Vector3dc> var4, Db var5, Db var6);

   Df a(VsiConvexPolygonc var1, VsiConvexPolygonc var2, Vector3dc var3, Iterator<? extends Vector3dc> var4);
}
