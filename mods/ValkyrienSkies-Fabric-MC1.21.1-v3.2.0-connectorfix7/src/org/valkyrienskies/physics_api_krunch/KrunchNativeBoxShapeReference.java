package org.valkyrienskies.physics_api_krunch;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.shadow.GT;
import org.valkyrienskies.core.impl.shadow.Hj;
import org.valkyrienskies.core.impl.shadow.Hl;
import org.valkyrienskies.core.impl.shadow.IB;

class KrunchNativeBoxShapeReference implements GT, IB {
   private long a;
   private static final long b = -1L;

   protected KrunchNativeBoxShapeReference(long shapeAddress) {
      this.a = shapeAddress;
   }

   @Override
   public void close() {
      this.d();
      if (!deleteBoxShape(this.a)) {
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
   public final Vector3dc a() {
      this.d();
      double[] var1 = new double[3];
      if (!getLengths(this.a, var1)) {
         throw new IllegalStateException("Call to getLengths() failed");
      } else {
         return new Vector3d(var1[0], var1[1], var1[2]);
      }
   }

   @Override
   public final void a(Vector3dc var1) {
      this.d();
      if (!setLengths(this.a, new double[]{var1.x(), var1.y(), var1.z()})) {
         throw new IllegalStateException("Call to setLengths() failed");
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

   private static native boolean getLengths(long var0, double[] var2);

   private static native boolean setLengths(long var0, double[] var2);

   private static native boolean deleteBoxShape(long var0);
}
