package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;

public class gA<E> extends gv<E> {
   private static final long b = 8692300188161871514L;
   protected final fQ<? super E, ? extends E> a;

   public static <E> gA<E> a(Collection<E> var0, fQ<? super E, ? extends E> var1) {
      return new gA<>(var0, var1);
   }

   private static <E> gA<E> b(Collection<E> var0, fQ<? super E, ? extends E> var1) {
      gA var2 = new gA(var0, var1);
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

   public gA(Collection<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Transformer must not be null");
      }

      this.a = var2;
   }

   public final E b(E var1) {
      return (E)this.a.a((E)var1);
   }

   protected final Collection<E> b(Collection<? extends E> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for (Object var3 : var1) {
         var2.add(this.b((E)var3));
      }

      return var2;
   }

   @Override
   public boolean add(E object) {
      return this.f().add(this.b(object));
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      return this.f().addAll(this.b(coll));
   }
}
