package org.valkyrienskies.core.impl.shadow;

public class dF {
   private static final char a = '0';
   private static final char b = '1';
   private static final char c = '9';
   private static final char d = '-';
   private static final char e = '+';
   private static final char f = '.';
   private static final char g = 'E';
   private static final char h = 'e';

   public static boolean a(String var0) {
      if (var0 != null && !var0.equals("")) {
         int var1 = 0;
         if (var0.charAt(0) == '-' || var0.charAt(0) == '+') {
            if (var0.length() == 1) {
               return false;
            }

            var1 = 1;
         }

         while (var1 < var0.length()) {
            char var2;
            if ((var2 = var0.charAt(var1)) < '0' || var2 > '9') {
               return false;
            }

            var1++;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean b(String var0) {
      return "true".equals(var0) || "false".equals(var0);
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   public static boolean c(String var0) {
      if (var0 != null && !var0.equals("")) {
         int var1 = 0;
         int var2 = var0.length();
         if (var0.charAt(0) == '-' || var0.charAt(0) == '+') {
            if (var0.length() == 1) {
               return false;
            }

            var1 = 1;
         }

         char var3;
         if ((var3 = var0.charAt(var1++)) == '0') {
            if (var1 < var2 && (var3 = var0.charAt(var1++)) != '.' && var3 != 'E' && var3 != 'e') {
               return false;
            }
         } else {
            if ('1' > var3 || var3 > '9') {
               return false;
            }

            while (var1 < var2 && '0' <= var3 && var3 <= '9') {
               var3 = var0.charAt(var1++);
            }
         }

         if (var3 == '.') {
            if (var1 >= var2) {
               return false;
            }

            do {
               var3 = var0.charAt(var1++);
            } while (var1 < var2 && '0' <= var3 && var3 <= '9');
         }

         if (var3 == 'E' || var3 == 'e') {
            if (var1 >= var2) {
               return false;
            }

            if ((var3 = var0.charAt(var1++)) != '+') {
               if (var3 == '-') {
                  if (var1 >= var2) {
                     return false;
                  }
               } else if (var1 >= var2 || '0' > var3 || var3 > '9') {
                  return var1 >= var2 && '0' <= var3 && var3 <= '9';
               }
            } else if (var1 >= var2) {
               return false;
            }

            do {
               var3 = var0.charAt(var1++);
            } while (var1 < var2 && '0' <= var3 && var3 <= '9');
         }

         return var1 >= var2 && '0' <= var3 && var3 <= '9';
      } else {
         return false;
      }
   }
}
