package org.valkyrienskies.core.impl.shadow;

import java.util.EnumSet;
import java.util.Set;

public class cU {
   public EnumSet<cU.a> a(long var1) {
      EnumSet var3 = EnumSet.noneOf(cU.a.class);
      cU.a[] var4;
      int var5 = (var4 = cU.a.values()).length;

      for (int var6 = 0; var6 < var5; var6++) {
         cU.a var7;
         long var8;
         if (((var8 = (var7 = var4[var6]).versionFlagValue) & var1) == var8) {
            var3.add(var7);
         }
      }

      return var3;
   }

   public long a(Set<cU.a> var1) {
      long var2 = 0L;

      for (cU.a var4 : var1) {
         var2 |= var4.versionFlagValue;
      }

      return var2;
   }

   public enum a {
      V4(1L),
      V6(2L),
      V7(4L),
      V201909(8L),
      V202012(16L);

      private final long versionFlagValue;

      a(long var3) {
         this.versionFlagValue = var3;
      }

      public final long a() {
         return this.versionFlagValue;
      }
   }
}
