package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class gF<E> implements Serializable, Comparator<E> {
   private static final long b = -721644942746081630L;
   public final List<Comparator<E>> a;
   private BitSet c = null;
   private boolean d = false;

   public gF() {
      this(new ArrayList<>(), new BitSet());
   }

   private gF(Comparator<E> var1) {
      this(var1, false);
   }

   private gF(Comparator<E> var1, boolean var2) {
      this.a = new ArrayList<>(1);
      this.a.add(var1);
      this.c = new BitSet(1);
   }

   private gF(List<Comparator<E>> var1) {
      this(var1, new BitSet(var1.size()));
   }

   private gF(List<Comparator<E>> var1, BitSet var2) {
      this.a = var1;
      this.c = var2;
   }

   public final void a(Comparator<E> var1) {
      this.a(var1, false);
   }

   private void a(Comparator<E> var1, boolean var2) {
      this.a();
      this.a.add(var1);
   }

   private void a(int var1, Comparator<E> var2) {
      boolean var4 = false;
      Comparator var3 = var2;
      int var6 = var1;
      gF var5 = this;
      this.a();
      var5.a.set(var6, var3);
      var5.c.clear(var6);
   }

   private void a(int var1, Comparator<E> var2, boolean var3) {
      this.a();
      this.a.set(var1, var2);
      this.c.clear(var1);
   }

   private void a(int var1) {
      this.a();
      this.c.clear(var1);
   }

   private void b(int var1) {
      this.a();
      this.c.set(var1);
   }

   private int b() {
      return this.a.size();
   }

   private boolean c() {
      return this.d;
   }

   public void a() {
      if (this.d) {
         throw new UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed");
      }
   }

   private void d() {
      if (this.a.size() == 0) {
         throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator");
      }
   }

   @Override
   public final int compare(E o1, E o2) {
      if (!this.d) {
         if (this.a.size() == 0) {
            throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator");
         }

         this.d = true;
      }

      Iterator var3 = this.a.iterator();

      for (int var4 = 0; var3.hasNext(); var4++) {
         int var5;
         if ((var5 = ((Comparator)var3.next()).compare(o1, o2)) != 0) {
            if (this.c.get(var4)) {
               if (var5 > 0) {
                  var5 = -1;
               } else {
                  var5 = 1;
               }
            }

            return var5;
         }
      }

      return 0;
   }

   @Override
   public final int hashCode() {
      int var1 = 0;
      if (this.a != null) {
         var1 = 0 ^ this.a.hashCode();
      }

      if (this.c != null) {
         var1 ^= this.c.hashCode();
      }

      return var1;
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (object == null) {
         return false;
      }

      if (!object.getClass().equals(this.getClass())) {
         return false;
      }

      object = (gF)object;
      return (this.c == null ? ((gF)object).c == null : this.c.equals(((gF)object).c))
         && (this.a == null ? ((gF)object).a == null : this.a.equals(((gF)object).a));
   }
}
