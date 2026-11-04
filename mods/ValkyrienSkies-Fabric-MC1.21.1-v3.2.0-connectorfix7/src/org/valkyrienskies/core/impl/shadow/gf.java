package org.valkyrienskies.core.impl.shadow;

import java.util.Set;

public class gf<E> extends gA<E> implements eW<E> {
   private static final long b = 5421170911299074185L;

   public static <E> eW<E> a(eW<E> var0, fQ<? super E, ? extends E> var1) {
      return new gf<>(var0, var1);
   }

   private static <E> eW<E> b(eW<E> var0, fQ<? super E, ? extends E> var1) {
      gf var2 = new gf(var0, var1);
      if (var0.size() > 0) {
         Object[] var3 = var0.toArray();
         var0.clear();
         Object[] var6 = var3;
         int var7 = var3.length;

         for (int var4 = 0; var4 < var7; var4++) {
            Object var5 = var6[var4];
            var2.f().add(var1.a(var5));
         }
      }

      return var2;
   }

   protected gf(eW<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1, var2);
   }

   private eW<E> b() {
      return (eW<E>)this.f();
   }

   @Override
   public boolean equals(Object object) {
      return object == this || this.f().equals(object);
   }

   @Override
   public int hashCode() {
      return this.f().hashCode();
   }

   @Override
   public final int a(Object var1) {
      return ((eW)this.f()).a(var1);
   }

   @Override
   public final boolean b(Object var1, int var2) {
      return ((eW)this.f()).b(var1, var2);
   }

   @Override
   public final boolean a(E var1, int var2) {
      return ((eW)this.f()).a(this.b((E)var1), var2);
   }

   @Override
   public final Set<E> a() {
      return kz.a(((eW)this.f()).a(), super.a);
   }
}
