package org.valkyrienskies.core.impl.shadow;

public abstract class sY {
   private final Cf a;

   public sY(int var1) {
      this.a = new Cf(var1);
   }

   public sY(Cf var1) {
      Ci.a(var1);
      this.a = var1;
   }

   protected static void a(tp var0, tu var1, tu var2) {
      Ci.a(var0);
      Ci.a(var1);
      Ci.a(var2);
      if (var0.f() != var0.g()) {
         throw new ti(var0.f(), var0.g());
      }

      if (var1.f() != var0.f()) {
         throw new oS(var1.f(), var0.f());
      }

      if (var2.f() != var0.g()) {
         throw new oS(var2.f(), var0.g());
      }
   }

   public final Cf a() {
      return this.a;
   }

   public tu a(tp var1, tu var2) {
      Ci.a(var1);
      sA var3;
      (var3 = new sA(var1.g())).f(0.0);
      return this.c(var1, var2, var3);
   }

   public tu b(tp var1, tu var2, tu var3) {
      Ci.a(var3);
      return this.c(var1, var2, var3.j());
   }

   public abstract tu c(tp var1, tu var2, tu var3);
}
