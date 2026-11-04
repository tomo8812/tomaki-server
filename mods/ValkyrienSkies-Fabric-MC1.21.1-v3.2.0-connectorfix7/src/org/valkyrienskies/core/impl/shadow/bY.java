package org.valkyrienskies.core.impl.shadow;

import java.text.MessageFormat;

public class bY implements ce {
   private final String a;
   private final MessageFormat b;

   private bY(String var1, MessageFormat var2) {
      this.a = var1;
      this.b = var2;
   }

   public static ce a(String var0) {
      return new bY(var0, null);
   }

   public static ce a(String var0, MessageFormat var1) {
      return new bY(var0, var1);
   }

   @Override
   public String b_() {
      return this.a;
   }

   @Override
   public MessageFormat b() {
      return this.b;
   }
}
