package org.valkyrienskies.core.impl.shadow;

public enum xd {
   EQ("="),
   LEQ("<="),
   GEQ(">=");

   private final String stringValue;

   xd(String var3) {
      this.stringValue = var3;
   }

   @Override
   public final String toString() {
      return this.stringValue;
   }

   public final xd a() {
      switch (this) {
         case LEQ:
            return GEQ;
         case GEQ:
            return LEQ;
         default:
            return EQ;
      }
   }
}
