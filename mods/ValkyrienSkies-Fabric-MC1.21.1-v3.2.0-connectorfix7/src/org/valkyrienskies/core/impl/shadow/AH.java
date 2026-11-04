package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public class AH extends AC implements Serializable {
   private static final long f = 3942403127395076445L;
   protected double e;

   public AH() {
      this.e = Double.NaN;
   }

   private AH(AH var1) {
      super(var1);
      this.e = var1.e;
   }

   @Override
   public void a(double var1) {
      if (super.a < 1L) {
         super.b = this.e = 0.0;
      }

      super.a(var1);
      this.e = this.e + (super.a - 1.0) * super.c * super.d;
   }

   @Override
   public void b() {
      super.b();
      this.e = Double.NaN;
   }

   @Override
   public double c() {
      return this.e;
   }

   public AH i() {
      AH var1 = new AH();
      a(this, var1);
      return var1;
   }

   public static void a(AH var0, AH var1) {
      Ci.a(var0);
      Ci.a(var1);
      AC.a(var0, var1);
      var1.e = var0.e;
   }

   // $VF: synthetic method
   @Override
   public AC f() {
      return this.i();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final long g_() {
      return super.g_();
   }

   // $VF: synthetic method
   @Override
   public An a() {
      return this.i();
   }

   // $VF: synthetic method
   @Override
   public AA d() {
      return this.i();
   }
}
