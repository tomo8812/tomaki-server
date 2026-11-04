package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class iJ<K> implements Serializable {
   private static final long b = 4465448607415788805L;
   public final K[] a;
   private transient int c;

   public iJ(K var1, K var2) {
      this((K[])(new Object[]{var1, var2}), false);
   }

   public iJ(K var1, K var2, K var3) {
      this((K[])(new Object[]{var1, var2, var3}), false);
   }

   public iJ(K var1, K var2, K var3, K var4) {
      this((K[])(new Object[]{var1, var2, var3, var4}), false);
   }

   public iJ(K var1, K var2, K var3, K var4, K var5) {
      this((K[])(new Object[]{var1, var2, var3, var4, var5}), false);
   }

   private iJ(K[] var1) {
      this((K[])var1, true);
   }

   private iJ(K[] var1, boolean var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("The array of keys must not be null");
      }

      if (var2) {
         this.a = (K[])((Object[])var1.clone());
      } else {
         this.a = (K[])var1;
      }

      this.a(var1);
   }

   private K[] b() {
      return (K[])((Object[])this.a.clone());
   }

   public final K a(int var1) {
      return this.a[var1];
   }

   public final int a() {
      return this.a.length;
   }

   @Override
   public final boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (other instanceof iJ) {
         other = (iJ)other;
         return Arrays.equals(this.a, ((iJ)other).a);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return this.c;
   }

   @Override
   public final String toString() {
      return "MultiKey" + Arrays.toString(this.a);
   }

   private void a(Object[] var1) {
      int var2 = 0;
      Object[] var6;
      int var3 = (var6 = var1).length;

      for (int var4 = 0; var4 < var3; var4++) {
         Object var5;
         if ((var5 = var6[var4]) != null) {
            var2 ^= var5.hashCode();
         }
      }

      this.c = var2;
   }

   private iJ c() {
      this.a(this.a);
      return this;
   }
}
