package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;

public final class gT<T> implements Serializable, fF<T> {
   private static final long a = -1863209236504077399L;
   private final T b;
   private final Comparator<T> c;
   private final gT.a d;

   private static <T> fF<T> a(T var0, Comparator<T> var1) {
      gT.a var2 = gT.a.EQUAL;
      var1 = var1;
      var0 = var0;
      if (var1 == null) {
         throw new NullPointerException("Comparator must not be null.");
      } else if (var2 == null) {
         throw new NullPointerException("Criterion must not be null.");
      } else {
         return new gT<>((T)var0, var1, var2);
      }
   }

   private static <T> fF<T> a(T var0, Comparator<T> var1, gT.a var2) {
      if (var1 == null) {
         throw new NullPointerException("Comparator must not be null.");
      } else if (var2 == null) {
         throw new NullPointerException("Criterion must not be null.");
      } else {
         return new gT<>((T)var0, var1, var2);
      }
   }

   private gT(T var1, Comparator<T> var2, gT.a var3) {
      this.b = (T)var1;
      this.c = var2;
      this.d = var3;
   }

   @Override
   public final boolean a(T var1) {
      var1 = this.c.compare(this.b, (T)var1);
      boolean var3;
      switch (this.d) {
         case EQUAL:
            var3 = var1 == 0;
            break;
         case GREATER:
            var3 = var1 > 0;
            break;
         case LESS:
            var3 = var1 < 0;
            break;
         case GREATER_OR_EQUAL:
            var3 = var1 >= 0;
            break;
         case LESS_OR_EQUAL:
            var3 = var1 <= 0;
            break;
         default:
            throw new IllegalStateException("The current criterion '" + this.d + "' is invalid.");
      }

      return var3;
   }

   public enum a {
      EQUAL,
      GREATER,
      LESS,
      GREATER_OR_EQUAL,
      LESS_OR_EQUAL;
   }
}
