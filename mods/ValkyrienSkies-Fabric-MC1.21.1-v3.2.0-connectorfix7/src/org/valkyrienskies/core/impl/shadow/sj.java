package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class sj implements Serializable, qN {
   private static final long a = 20131218L;

   private sj() {
   }

   public static sj c() {
      return sj.a.a;
   }

   @Override
   public final int a() {
      return 1;
   }

   @Override
   public final qN b() {
      throw new sj.b();
   }

   private static sj d() {
      return sj.a.a;
   }

   // $VF: synthetic method
   sj(byte var1) {
      this();
   }

   public static class a {
      private static final sj a = new sj((byte)0);

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
