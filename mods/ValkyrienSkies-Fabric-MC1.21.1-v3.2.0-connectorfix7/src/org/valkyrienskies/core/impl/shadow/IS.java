package org.valkyrienskies.core.impl.shadow;

import java.nio.ByteBuffer;

public final class IS {
   private IS() {
   }

   public static void a(In var0, ByteBuffer var1) {
      var1.putInt(var0.b);
      var1.putInt(var0.c);
      var1.putInt(var0.d);

      for (int var2 = 0; var2 < 16; var2++) {
         for (int var3 = 0; var3 < 16; var3++) {
            for (int var4 = 0; var4 < 16; var4++) {
               var1.putInt(var0.a(var2, var3, var4));
            }
         }
      }
   }

   public static void a(Ir var0, ByteBuffer var1) {
      var1.putInt(var0.b);
      var1.putInt(var0.c);
      var1.putInt(var0.d);
      var1.putInt(var0.e.size());

      for (int var2 = 0; var2 < var0.e.size(); var2++) {
         short var3 = var0.e.getShort(var2);
         int var4 = var0.f.getInt(var2);
         var1.putShort(var3);
         var1.putInt(var4);
      }
   }
}
