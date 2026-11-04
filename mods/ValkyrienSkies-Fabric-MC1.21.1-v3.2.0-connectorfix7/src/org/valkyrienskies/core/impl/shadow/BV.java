package org.valkyrienskies.core.impl.shadow;

public final class BV implements kM<BU> {
   private static final BV a = new BV();

   private BV() {
   }

   public static final BV d() {
      return a;
   }

   private static BU e() {
      return BU.a;
   }

   private static BU f() {
      return BU.b;
   }

   @Override
   public final Class<? extends kN<BU>> c() {
      return BU.class;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN b() {
      return BU.b;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a() {
      return BU.a;
   }
}
