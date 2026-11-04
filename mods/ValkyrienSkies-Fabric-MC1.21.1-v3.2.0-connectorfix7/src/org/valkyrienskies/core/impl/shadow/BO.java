package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class BO implements Serializable, kM<BN> {
   private static final long a = 4756431066541037559L;

   private BO() {
   }

   public static BO d() {
      return BO.a.a;
   }

   private static BN e() {
      return BN.b;
   }

   private static BN f() {
      return BN.a;
   }

   @Override
   public final Class<? extends kN<BN>> c() {
      return BN.class;
   }

   private static BO g() {
      return BO.a.a;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN b() {
      return BN.b;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a() {
      return BN.a;
   }

   // $VF: synthetic method
   BO(byte var1) {
      this();
   }

   static class a {
      private static final BO a = new BO((byte)0);

      private a() {
      }
   }
}
