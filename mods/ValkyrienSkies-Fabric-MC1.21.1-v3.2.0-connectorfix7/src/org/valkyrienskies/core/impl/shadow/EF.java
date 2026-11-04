package org.valkyrienskies.core.impl.shadow;

import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;

public final class EF implements ek<ChunkAllocatorProvider> {
   private static ChunkAllocatorProvider b() {
      return ev.b(EE.a.a.a());
   }

   public static EF a() {
      return EF.a.a;
   }

   private static ChunkAllocatorProvider c() {
      return ev.b(EE.a.a.a());
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      return ev.b(EE.a.a.a());
   }

   static final class a {
      static final EF a = new EF();

      private a() {
      }
   }
}
