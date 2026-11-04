package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dt implements Serializable {
   private static final long a = -8832409930574867162L;
   private final Pattern[] b;

   public dt(String var1) {
      this(var1, true);
   }

   public dt(String var1, boolean var2) {
      this(new String[]{var1}, var2);
   }

   public dt(String[] var1) {
      this(var1, true);
   }

   public dt(String[] var1, boolean var2) {
      if (var1 != null && var1.length != 0) {
         this.b = new Pattern[var1.length];
         var2 = var2 ? 0 : 2;

         for (int var3 = 0; var3 < var1.length; var3++) {
            if (var1[var3] == null || var1[var3].length() == 0) {
               throw new IllegalArgumentException("Regular expression[" + var3 + "] is missing");
            }

            this.b[var3] = Pattern.compile(var1[var3], var2);
         }
      } else {
         throw new IllegalArgumentException("Regular expressions are missing");
      }
   }

   public boolean a(String var1) {
      if (var1 == null) {
         return false;
      }

      for (int var2 = 0; var2 < this.b.length; var2++) {
         if (this.b[var2].matcher(var1).matches()) {
            return true;
         }
      }

      return false;
   }

   public String[] b(String var1) {
      if (var1 == null) {
         return null;
      }

      for (int var2 = 0; var2 < this.b.length; var2++) {
         Matcher var3;
         if ((var3 = this.b[var2].matcher(var1)).matches()) {
            int var5;
            String[] var6 = new String[var5 = var3.groupCount()];

            for (int var4 = 0; var4 < var5; var4++) {
               var6[var4] = var3.group(var4 + 1);
            }

            return var6;
         }
      }

      return null;
   }

   public String c(String var1) {
      if (var1 == null) {
         return null;
      }

      for (int var2 = 0; var2 < this.b.length; var2++) {
         Matcher var3;
         if ((var3 = this.b[var2].matcher(var1)).matches()) {
            int var6;
            if ((var6 = var3.groupCount()) == 1) {
               return var3.group(1);
            }

            StringBuilder var7 = new StringBuilder();

            for (int var4 = 0; var4 < var6; var4++) {
               String var5;
               if ((var5 = var3.group(var4 + 1)) != null) {
                  var7.append(var5);
               }
            }

            return var7.toString();
         }
      }

      return null;
   }

   @Override
   public String toString() {
      StringBuilder var1;
      (var1 = new StringBuilder()).append("RegexValidator{");

      for (int var2 = 0; var2 < this.b.length; var2++) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(this.b[var2].pattern());
      }

      var1.append("}");
      return var1.toString();
   }
}
