package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class gG<T> implements Serializable, Comparator<T> {
   private static final long a = 82794675842863201L;
   private final Map<T, Integer> b = new HashMap<>();
   private int c = 0;
   private boolean d = false;
   private gG.a e = gG.a.EXCEPTION;

   public gG() {
   }

   private gG(T... var1) {
      if (var1 == null) {
         throw new NullPointerException("The list of items must not be null");
      }

      Object[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         Object var4 = var5[var3];
         this.a((T)var4);
      }
   }

   private gG(List<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("The list of items must not be null");
      }

      for (Object var2 : var1) {
         this.a((T)var2);
      }
   }

   private boolean a() {
      return this.d;
   }

   private void b() {
      if (this.d) {
         throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
      }
   }

   private gG.a c() {
      return this.e;
   }

   private void a(gG.a var1) {
      this.b();
      if (var1 == null) {
         throw new NullPointerException("Unknown object behavior must not be null");
      }

      this.e = var1;
   }

   private boolean a(T var1) {
      this.b();
      return this.b.put((T)var1, this.c++) == null;
   }

   private boolean a(T var1, T var2) {
      this.b();
      Integer var3;
      if ((var3 = this.b.get(var1)) == null) {
         throw new IllegalArgumentException(var1 + " not known to " + this);
      } else {
         return this.b.put((T)var2, var3) == null;
      }
   }

   @Override
   public final int compare(T obj1, T obj2) {
      this.d = true;
      Integer var3 = this.b.get(obj1);
      Integer var4 = this.b.get(obj2);
      if (var3 != null && var4 != null) {
         return var3.compareTo(var4);
      }

      switch (this.e) {
         case BEFORE:
            if (var3 == null) {
               if (var4 == null) {
                  return 0;
               }

               return -1;
            }

            return 1;
         case AFTER:
            if (var3 == null) {
               if (var4 == null) {
                  return 0;
               }

               return 1;
            }

            return -1;
         case EXCEPTION:
            obj1 = var3 == null ? obj1 : obj2;
            throw new IllegalArgumentException("Attempting to compare unknown object " + obj1);
         default:
            throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + this.e);
      }
   }

   @Override
   public final int hashCode() {
      int var1;
      return (((var1 = 629 + (this.b == null ? 0 : this.b.hashCode())) * 37 + (this.e == null ? 0 : this.e.hashCode())) * 37 + this.c) * 37 + (this.d ? 0 : 1);
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

      object = (gG)object;
      return (this.b == null ? ((gG)object).b == null : this.b.equals(((gG)object).b))
         && (
            this.e == null
               ? ((gG)object).e == null
               : this.e == ((gG)object).e && this.c == ((gG)object).c && this.d == ((gG)object).d && this.e == ((gG)object).e
         );
   }

   public enum a {
      BEFORE,
      AFTER,
      EXCEPTION;
   }
}
