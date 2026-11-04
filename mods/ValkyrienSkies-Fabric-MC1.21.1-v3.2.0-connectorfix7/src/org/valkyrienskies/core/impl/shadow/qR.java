package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class qR<S extends qN, P extends qM<S>> implements Serializable {
   private static final long d = 20140126L;
   public final P a;
   public final double b;
   public final P[] c;

   public qR(P var1, double var2, P... var4) {
      this.a = (P)var1;
      this.b = var2;
      this.c = (P[])((qM[])var4.clone());
   }

   public final P a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   public final P[] c() {
      return (P[])((qM[])this.c.clone());
   }

   public final int d() {
      return this.c.length;
   }

   private boolean a(P var1) {
      return var1.a(this.a) <= this.b;
   }

   public final boolean a(P var1, double var2) {
      return var1.a(this.a) <= this.b + var2;
   }
}
