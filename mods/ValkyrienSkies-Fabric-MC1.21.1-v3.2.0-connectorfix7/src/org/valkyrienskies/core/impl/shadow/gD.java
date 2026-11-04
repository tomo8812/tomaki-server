package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;

public final class gD implements Serializable, Comparator<Boolean> {
   private static final long a = 1830042991606340609L;
   private static final gD b = new gD(true);
   private static final gD c = new gD(false);
   private boolean d = false;

   private static gD a() {
      return b;
   }

   private static gD b() {
      return c;
   }

   public static gD a(boolean var0) {
      return var0 ? b : c;
   }

   public gD() {
      this(false);
   }

   private gD(boolean var1) {
      this.d = var1;
   }

   private int a(Boolean var1, Boolean var2) {
      boolean var3 = var1;
      boolean var4 = var2;
      if (var3 ^ var4) {
         return var3 ^ this.d ? 1 : -1;
      } else {
         return 0;
      }
   }

   @Override
   public final int hashCode() {
      int var1 = "BooleanComparator".hashCode();
      return this.d ? -1 * var1 : var1;
   }

   @Override
   public final boolean equals(Object object) {
      return this == object || object instanceof gD && this.d == ((gD)object).d;
   }

   private boolean c() {
      return this.d;
   }

   // $VF: synthetic method
   @Override
   public final int compare(Object var1, Object var2) {
      Boolean var10001 = (Boolean)var1;
      Boolean var3 = var2;
      var2 = var10001;
      gD var4 = this;
      boolean var6 = var2;
      boolean var7 = var3;
      if (var6 ^ var7) {
         return var6 ^ var4.d ? 1 : -1;
      } else {
         return 0;
      }
   }
}
