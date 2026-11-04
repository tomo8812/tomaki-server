package org.valkyrienskies.core.impl.shadow;

public abstract class tm extends sY {
   public tm(int var1) {
      super(var1);
   }

   public tm(Cf var1) {
      super(var1);
   }

   public tu b(tp var1, tp var2, tu var3, tu var4) {
      Ci.a(var4);
      return this.a(var1, var2, var3, var4.j());
   }

   @Override
   public tu a(tp var1, tu var2) {
      Ci.a(var1);
      sA var3;
      (var3 = new sA(var1.g())).f(0.0);
      return this.a(var1, null, var2, var3);
   }

   @Override
   public tu b(tp var1, tu var2, tu var3) {
      Ci.a(var3);
      return this.a(var1, null, var2, var3.j());
   }

   protected static void c(tp var0, tp var1, tu var2, tu var3) {
      tu var4 = var3;
      var3 = var2;
      tp var5 = var0;
      Ci.a(var0);
      Ci.a(var3);
      Ci.a(var4);
      if (var5.f() != var5.g()) {
         throw new ti(var5.f(), var5.g());
      }

      if (var3.f() != var5.f()) {
         throw new oS(var3.f(), var5.f());
      }

      if (var4.f() != var5.g()) {
         throw new oS(var4.f(), var5.g());
      }

      if (var1 != null) {
         if (var1.g() != var1.f()) {
            throw new ti(var1.g(), var1.f());
         }

         if (var1.f() != var0.f()) {
            throw new oS(var1.f(), var0.f());
         }
      }
   }

   public tu a(tp var1, tp var2, tu var3) {
      Ci.a(var1);
      sA var4 = new sA(var1.g());
      return this.a(var1, var2, var3, var4);
   }

   public abstract tu a(tp var1, tp var2, tu var3, tu var4);

   @Override
   public tu c(tp var1, tu var2, tu var3) {
      return this.a(var1, null, var2, var3);
   }
}
