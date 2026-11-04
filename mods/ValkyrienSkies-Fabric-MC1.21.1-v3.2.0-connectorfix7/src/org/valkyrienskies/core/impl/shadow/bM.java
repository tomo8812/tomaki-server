package org.valkyrienskies.core.impl.shadow;

public abstract class bM implements ci {
   private final String a;
   private final String b;

   public bM(String var1) {
      this(var1, "");
   }

   public bM(String var1, String var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public String a() {
      return this.a;
   }

   @Override
   public String b() {
      return this.b;
   }
}
