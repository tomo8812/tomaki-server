package org.valkyrienskies.core.impl.shadow;

public final class Ax extends Aq {
   private static final long a = 1L;

   public Ax() {
      this(-1);
   }

   private Ax(int var1) {
      super(-1);
   }

   private Ax(Ax var1) {
      a(var1, this);
   }

   @Override
   public final synchronized void a(double var1) {
      super.a(var1);
   }

   @Override
   public final synchronized double a(AA var1) {
      return super.a(var1);
   }

   @Override
   public final synchronized void i() {
      super.i();
   }

   @Override
   public final synchronized double b(int var1) {
      return super.b(var1);
   }

   @Override
   public final synchronized long d() {
      return super.d();
   }

   @Override
   public final synchronized double e() {
      return super.e();
   }

   @Override
   public final synchronized double h() {
      return super.h();
   }

   @Override
   public final synchronized double[] k() {
      return super.k();
   }

   @Override
   public final synchronized int j() {
      return super.j();
   }

   @Override
   public final synchronized void a(int var1) {
      super.a(var1);
   }

   @Override
   public final synchronized String toString() {
      return super.toString();
   }

   private synchronized Ax m() {
      Ax var1 = new Ax();
      a(this, var1);
      return var1;
   }

   private static void a(Ax var0, Ax var1) {
      Ci.a(var0);
      Ci.a(var1);
      synchronized (var0) {
         synchronized (var1) {
            Aq.a(var0, var1);
         }
      }
   }

   // $VF: synthetic method
   @Override
   public final Aq l() {
      return this.m();
   }
}
