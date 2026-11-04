package org.valkyrienskies.physics_api_krunch;

import org.valkyrienskies.core.impl.shadow.GU;
import org.valkyrienskies.core.impl.shadow.Hj;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.IB;

class KrunchNativeCapsuleShapeReference implements GU, IB {
   private long a;
   private static final long b = -1L;

   protected KrunchNativeCapsuleShapeReference(long shapeAddress) {
      this.a = shapeAddress;
   }

   @Override
   public void close() {
      this.d();
      if (!deleteCapsuleShape(this.a)) {
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
   public final double h_() {
      this.d();
      double[] var1 = new double[1];
      if (!getHalfLength(this.a, var1)) {
         throw new IllegalStateException("Call to getLengths() failed");
      } else {
         return var1[0];
      }
   }

   @Override
   public final void b(double var1) {
      this.d();
      setHalfLength(this.a, var1);
   }

   @Override
   public final double a() {
      this.d();
      double[] var1 = new double[1];
      if (!getRadius(this.a, var1)) {
         throw new IllegalStateException("Call to getLengths() failed");
      } else {
         return var1[0];
      }
   }

   @Override
   public final void a(double var1) {
      this.d();
      setRadius(this.a, var1);
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

   private static native boolean getHalfLength(long var0, double[] var2);

   private static native boolean setHalfLength(long var0, double var2);

   private static native boolean getRadius(long var0, double[] var2);

   private static native boolean setRadius(long var0, double var2);

   private static native boolean deleteCapsuleShape(long var0);
}
