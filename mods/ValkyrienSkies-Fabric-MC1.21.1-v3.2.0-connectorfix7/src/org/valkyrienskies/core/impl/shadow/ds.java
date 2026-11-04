package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ds implements Serializable {
   private static final int a = 255;
   private static final int b = 65535;
   private static final int c = 16;
   private static final long d = -919201640201914789L;
   private static final String e = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";
   private static final int f = 8;
   private static final int g = 4;
   private static final ds h = new ds();
   private final dt i = new dt("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

   public static ds a() {
      return h;
   }

   public boolean a(String var1) {
      return this.b(var1) || this.c(var1);
   }

   public boolean b(String var1) {
      String[] var7;
      if ((var7 = this.i.b(var1)) == null) {
         return false;
      }

      String[] var8;
      int var2 = (var8 = var7).length;

      for (int var3 = 0; var3 < var2; var3++) {
         String var4;
         if ((var4 = var8[var3]) == null || var4.length() == 0) {
            return false;
         }

         int var5;
         try {
            var5 = Integer.parseInt(var4);
         } catch (NumberFormatException var6) {
            return false;
         }

         if (var5 > 255) {
            return false;
         }

         if (var4.length() > 1 && var4.startsWith("0")) {
            return false;
         }
      }

      return true;
   }

   public boolean c(String var1) {
      boolean var2;
      if ((var2 = var1.contains("::")) && var1.indexOf("::") != var1.lastIndexOf("::")) {
         return false;
      }

      if ((!var1.startsWith(":") || var1.startsWith("::")) && (!var1.endsWith(":") || var1.endsWith("::"))) {
         String[] var3 = var1.split(":");
         if (var2) {
            ArrayList var4 = new ArrayList<>(Arrays.asList(var3));
            if (var1.endsWith("::")) {
               var4.add("");
            } else if (var1.startsWith("::") && !var4.isEmpty()) {
               var4.remove(0);
            }

            var3 = var4.toArray(new String[var4.size()]);
         }

         if (var3.length > 8) {
            return false;
         }

         int var9 = 0;
         int var8 = 0;

         for (int var5 = 0; var5 < var3.length; var5++) {
            String var6;
            if ((var6 = var3[var5]).length() == 0) {
               if (++var8 > 1) {
                  return false;
               }
            } else {
               var8 = 0;
               if (var5 == var3.length - 1 && var6.contains(".")) {
                  if (!this.b(var6)) {
                     return false;
                  }

                  var9 += 2;
                  continue;
               }

               if (var6.length() > 4) {
                  return false;
               }

               try {
                  var10 = Integer.parseInt(var6, 16);
               } catch (NumberFormatException var7) {
                  return false;
               }

               if (var10 < 0 || var10 > 65535) {
                  return false;
               }
            }

            var9++;
         }

         return var9 <= 8 && (var9 >= 8 || var2);
      } else {
         return false;
      }
   }
}
