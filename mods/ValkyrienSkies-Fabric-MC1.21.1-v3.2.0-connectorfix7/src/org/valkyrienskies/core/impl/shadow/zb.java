package org.valkyrienskies.core.impl.shadow;

@Deprecated
public enum zb {
   EQ("="),
   LEQ("<="),
   GEQ(">=");

   private final String stringValue;

   zb(String var3) {
      this.stringValue = var3;
   }

   @Override
   public final String toString() {
      return this.stringValue;
   }

   public final zb a() {
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
