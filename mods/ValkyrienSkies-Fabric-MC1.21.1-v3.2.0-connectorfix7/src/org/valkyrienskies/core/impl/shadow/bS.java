package org.valkyrienskies.core.impl.shadow;

public class bS {
   static final bS a = new bS(false, false, false);
   private final boolean b;
   private final boolean c;
   private final boolean d;

   public bS(boolean var1, boolean var2, boolean var3) {
      if (!var1 && var2) {
         throw new IllegalArgumentException();
      }

      this.b = var1;
      this.c = var2;
      this.d = var3;
   }

   public boolean a() {
      return this.b;
   }

   public boolean b() {
      return this.c;
   }

   public boolean c() {
      return this.d;
   }
}
