package org.valkyrienskies.core.impl.shadow;

public final class ss extends rL<sq, sj> {
   public ss(rV<sq> var1, rY<sj> var2) {
      super(var1, var2);
   }

   @Override
   public final rL<sq, sj> a(rV<sq> var1, rY<sj> var2) {
      return new ss(var1, var2);
   }

   @Override
   public final sb.a<sq> a(rV<sq> var1) {
      sl var2 = (sl)this.c();
      var1 = var1;
      double var4;
      if (!((var4 = rs.a(var2.a, var1.a)) < var2.d) && !(var4 > Math.PI - var2.d)) {
         sf var7 = var2.a(var1);
         sg.b var8;
         sg var3 = (var8 = ((sg)this.d()).a(var7)).a;
         sg var9 = var8.b;
         return new sb.a<>(var3 == null ? null : new ss(var2.a(), var3), var9 == null ? null : new ss(var2.a(), var9));
      } else {
         return new sb.a<>(null, null);
      }
   }
}
