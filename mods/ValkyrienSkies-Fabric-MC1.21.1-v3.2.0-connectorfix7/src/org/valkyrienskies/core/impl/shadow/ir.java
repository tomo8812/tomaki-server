package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public final class ir<E> implements Iterator<List<E>> {
   private final int[] a;
   private final Map<Integer, E> b;
   private final boolean[] c;
   private List<E> d;

   public ir(Collection<? extends E> var1) {
      if (var1 == null) {
         throw new NullPointerException("The collection must not be null");
      }

      this.a = new int[var1.size()];
      this.c = new boolean[var1.size()];
      Arrays.fill(this.c, false);
      int var2 = 1;
      this.b = new HashMap<>();

      for (Object var4 : var1) {
         this.b.put(var2, (E)var4);
         this.a[var2 - 1] = var2++;
      }

      this.d = new ArrayList<>(var1);
   }

   @Override
   public final boolean hasNext() {
      return this.d != null;
   }

   public final List<E> a() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      int var1 = -1;
      int var2 = -1;

      for (int var3 = 0; var3 < this.a.length; var3++) {
         if ((this.c[var3] && var3 < this.a.length - 1 && this.a[var3] > this.a[var3 + 1] || !this.c[var3] && var3 > 0 && this.a[var3] > this.a[var3 - 1])
            && this.a[var3] > var2) {
            var2 = this.a[var3];
            var1 = var3;
         }
      }

      if (var2 == -1) {
         List var9 = this.d;
         this.d = null;
         return var9;
      }

      int var6 = this.c[var1] ? 1 : -1;
      int var4 = this.a[var1];
      this.a[var1] = this.a[var1 + var6];
      this.a[var1 + var6] = var4;
      boolean var10 = this.c[var1];
      this.c[var1] = this.c[var1 + var6];
      this.c[var1 + var6] = var10;
      ArrayList var5 = new ArrayList();

      for (int var7 = 0; var7 < this.a.length; var7++) {
         if (this.a[var7] > var2) {
            this.c[var7] = !this.c[var7];
         }

         var5.add(this.b.get(this.a[var7]));
      }

      List var8 = this.d;
      this.d = var5;
      return var8;
   }

   @Override
   public final void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }

   // $VF: synthetic method
   @Override
   public final Object next() {
      return this.a();
   }
}
