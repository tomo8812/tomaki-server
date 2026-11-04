package org.valkyrienskies.physics_api_krunch;

import org.valkyrienskies.core.impl.shadow.Hj;
import org.valkyrienskies.core.impl.shadow.Hk;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.IB;

class KrunchNativeSphereShapeReference implements Hk, IB {
   private long a;
   private static final long b = -1L;

   protected KrunchNativeSphereShapeReference(long shapeAddress) {
      this.a = shapeAddress;
   }

   @Override
   public void close() {
      this.d();
      if (!deleteSphereShape(this.a)) {
         throw new Hj("Delete box shape failed! This shape is currently in use!");
      }

      this.a = -1L;
   }

   @Override
   protected void finalize() {
      if (this.a != -1L) {
         this.close();
      }
   }

   @Override
   public final long b() {
      this.d();
      return this.a;
   }

   @Override
   public final double a() {
      this.d();
      double[] var1 = new double[1];
      if (!getRadius(this.a, var1)) {
         throw new IllegalStateException("Call to getRadius() failed");
      } else {
         return var1[0];
      }
   }

   @Override
   public final void a(double var1) {
      this.d();
      if (!setRadius(this.a, var1)) {
         throw new IllegalStateException("Call to setRadius() failed");
      }
   }

   @Override
   public final boolean j_() {
      return this.a == -1L;
   }

   private void d() {
      if (this.j_()) {
         throw new Hl("This shape has been deleted!");
      }
   }

   private static native boolean getRadius(long var0, double[] var2);

   private static native boolean setRadius(long var0, double var2);

   private static native boolean deleteSphereShape(long var0);
}
