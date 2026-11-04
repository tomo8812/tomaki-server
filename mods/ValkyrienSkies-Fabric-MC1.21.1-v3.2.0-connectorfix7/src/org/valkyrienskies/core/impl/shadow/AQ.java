package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AQ extends AT implements Serializable {
   private static final long a = -3961477041290915687L;
   private static final double b = 50.0;

   public AQ() {
      super(50.0);
   }

   private AQ(AQ var1) {
      super(var1);
   }

   private AQ(AT.a var1, Bp var2, Cg var3) {
      super(50.0, var1, var2, var3);
   }

   private AQ b(AT.a var1) {
      return new AQ(var1, this.b(), this.c());
   }

   private AQ b(Bp var1) {
      return new AQ(this.a(), var1, this.c());
   }

   private AQ b(Cg var1) {
      return new AQ(this.a(), this.b(), var1);
   }

   // $VF: synthetic method
   @Override
   public final AT a(Cg var1) {
      Cg var2 = var1;
      AQ var3 = this;
      return new AQ(var3.a(), var3.b(), var2);
   }

   // $VF: synthetic method
   @Override
   public final AT a(Bp var1) {
      Bp var2 = var1;
      AQ var3 = this;
      return new AQ(var3.a(), var2, var3.c());
   }

   // $VF: synthetic method
   @Override
   public final AT a(AT.a var1) {
      AT.a var2 = var1;
      AQ var3 = this;
      return new AQ(var2, var3.b(), var3.c());
   }
}
