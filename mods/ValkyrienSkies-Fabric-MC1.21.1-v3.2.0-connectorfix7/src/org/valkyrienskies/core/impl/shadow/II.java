package org.valkyrienskies.core.impl.shadow;

public final class II {
   public static final boolean a(byte var0) {
      byte var1 = var0;
      if (var0 == 0) {
         return false;
      } else if (var1 == 1) {
         return true;
      } else {
         throw new IllegalStateException("Tried converting byte " + var0 + " to boolean!");
      }
   }

   public static final byte a(boolean var0) {
      return (byte)(var0 ? 1 : 0);
   }

   // $VF: synthetic method
   public static final byte b(boolean var0) {
      return a(var0);
   }

   // $VF: synthetic method
   public static final boolean b(byte var0) {
      return a(var0);
   }
}
