package org.valkyrienskies.physics_api_krunch;

import org.valkyrienskies.core.impl.shadow.Hj;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.Hq;
import org.valkyrienskies.core.impl.shadow.IB;

class KrunchNativeWheelShapeReference implements Hq, IB {
   private long a;
   private static final long b = -1L;

   protected KrunchNativeWheelShapeReference(long shapeAddress) {
      this.a = shapeAddress;
   }

   @Override
   public void close() {
      this.d();
      if (!deleteWheelShape(this.a)) {
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
   public final double k_() {
      this.d();
      double[] var1 = new double[1];
      if (!getHalfThickness(this.a, var1)) {
         throw new IllegalStateException("Call to getHalfThickness() failed");
      } else {
         return var1[0];
      }
   }

   @Override
   public final void b(double var1) {
      this.d();
      if (!setHalfThickness(this.a, var1)) {
         throw new IllegalStateException("Call to setHalfThickness() failed");
      }
   }

   @Override
   public final double a() {
      this.d();
      double[] var1 = new double[1];
      if (!getWheelRadius(this.a, var1)) {
         throw new IllegalStateException("Call to getLengths() failed");
      } else {
         return var1[0];
      }
   }

   @Override
   public final void a(double var1) {
      this.d();
      if (!setWheelRadius(this.a, var1)) {
         throw new IllegalStateException("Call to setWheelRadius() failed");
      }
   }

   @Override
   public final int c() {
      this.d();
      int[] var1 = new int[1];
      if (!getPointShellSize(this.a, var1)) {
         throw new IllegalStateException("Call to getPointShellSize() failed");
      } else {
         return var1[0];
      }
   }

   @Override
   public final void a(int var1) {
      this.d();
      if (!setPointShellSize(this.a, var1)) {
         throw new IllegalStateException("Call to setPointShellSize() failed");
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

   private static native boolean getHalfThickness(long var0, double[] var2);

   private static native boolean setHalfThickness(long var0, double var2);

   private static native boolean getWheelRadius(long var0, double[] var2);

   private static native boolean setWheelRadius(long var0, double var2);

   private static native boolean getPointShellSize(long var0, int[] var2);

   private static native boolean setPointShellSize(long var0, int var2);

   private static native boolean deleteWheelShape(long var0);
}
