package org.valkyrienskies.core.impl.shadow;

import java.util.function.Predicate;

public final class bh implements aD {
   private final Predicate<ay> a;

   public static bh a() {
      return new bh(var0 -> var0.a.isPublic() && !var0.a.isStatic());
   }

   public static bh b() {
      return new bh(var0 -> !var0.a.isPublic() && !var0.a.isStatic() && var0.d());
   }

   public static bh c() {
      return new bh(var0 -> !var0.a.isPublic() && !var0.a.isStatic() && !var0.d());
   }

   public static bh d() {
      return new bh(ay::b);
   }

   private bh(Predicate<ay> var1) {
      this.a = var1;
   }

   @Override
   public final void a(aL var1) {
      var1.c.a(this.a);
   }
}
