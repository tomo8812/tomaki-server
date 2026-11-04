package org.valkyrienskies.core.impl.shadow;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class cl {
   private static final String a = "jsv-messages";
   private static final ResourceBundle b;

   public static String a(String var0) {
      String var1 = null;

      try {
         var1 = b.getString(var0);
      } catch (MissingResourceException var2) {
         var2.printStackTrace();
         System.exit(2);
      }

      return var1;
   }

   static {
      ResourceBundle var0 = null;

      try {
         var0 = ResourceBundle.getBundle("jsv-messages");
      } catch (MissingResourceException var2) {
         var2.printStackTrace();
         System.exit(1);
      }

      b = var0;
   }
}
