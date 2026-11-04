package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gV<I, O> implements Serializable, fQ<I, O> {
   private static final long a = 6374440726369055124L;
   private static gV b = new gV(null);
   private final O c;

   public static <I, O> fQ<I, O> a() {
      return b;
   }

   public static <I, O> fQ<I, O> b(O var0) {
      return var0 == null ? b : new gV<>((O)var0);
   }

   private gV(O var1) {
      this.c = (O)var1;
   }

   @Override
   public final O a(I var1) {
      return this.c;
   }

   private O b() {
      return this.c;
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      gV var2;
      Object var3;
      return !(obj instanceof gV) ? false : (var3 = (var2 = (gV)obj).c) == this.c || var3 != null && var3.equals(this.c);
   }

   @Override
   public final int hashCode() {
      int var1 = "ConstantTransformer".hashCode() << 2;
      if (this.c != null) {
         var1 |= this.c.hashCode();
      }

      return var1;
   }
}
