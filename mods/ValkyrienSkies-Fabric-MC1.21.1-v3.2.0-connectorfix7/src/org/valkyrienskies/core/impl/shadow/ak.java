package org.valkyrienskies.core.impl.shadow;

public class ak extends RuntimeException {
   private aq a;
   private am b;

   public ak(String var1, aq var2, am var3) {
      super(var1);
      this.a = var2;
      this.b = var3;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.a != null) {
         var1.append('[').append(this.a).append(" Operation] ");
      }

      var1.append(this.getMessage());
      if (this.b != null) {
         var1.append(" at ").append(this.b.a() ? "root" : this.b);
      }

      return var1.toString();
   }
}
