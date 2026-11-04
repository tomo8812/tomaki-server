package org.valkyrienskies.core.impl.shadow;

import java.util.Map;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface GX extends GW {
   Map<Integer, GX.a> a();

   GX.a a(int var1, GW var2, Hg var3, double var4, Vector3dc var6, Ha var7);

   boolean a(int var1);

   interface a {
      GW a();

      void a(GW var1);

      Hg b();

      void a(Hg var1);

      double c();

      void a(double var1);

      Vector3dc d();

      void a(Vector3dc var1);

      Ha e();

      void a(Ha var1);
   }

   final class b {
      // $VF: synthetic method
      private static GX.a a(GX var0, int var1, GW var2, Hg var3, double var4, Vector3dc var6, Ha var7, int var8, Object var9) {
         if (var9 != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: makeNewSegment");
         }

         if ((var8 & 4) != 0) {
            var3 = Hg.a.a();
         }

         if ((var8 & 8) != 0) {
            var4 = 1.0;
         }

         if ((var8 & 16) != 0) {
            var6 = new Vector3d();
         }

         if ((var8 & 32) != 0) {
            var7 = null;
         }

         return var0.a(var1, var2, var3, var4, var6, var7);
      }
   }
}
