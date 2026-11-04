package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class qU implements Serializable, qN {
   private static final long a = -1178039568877797126L;

   private qU() {
   }

   public static qU c() {
      return qU.a.a;
   }

   @Override
   public final int a() {
      return 1;
   }

   @Override
   public final qN b() {
      throw new qU.b();
   }

   private static qU d() {
      return qU.a.a;
   }

   // $VF: synthetic method
   qU(byte var1) {
      this();
   }

   public static class a {
      private static final qU a = new qU((byte)0);

      private a() {
      }
   }

   public static class b extends pb {
      private static final long a = 20140225L;

      public b() {
         super(px.NOT_SUPPORTED_IN_DIMENSION_N, 1);
      }
   }
}
