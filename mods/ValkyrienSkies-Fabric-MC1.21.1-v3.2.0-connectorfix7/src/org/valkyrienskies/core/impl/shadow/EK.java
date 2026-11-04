package org.valkyrienskies.core.impl.shadow;

import org.valkyrienskies.core.impl.game.ChunkAllocator;

public final class EK implements ek<ChunkAllocator> {
   private final EI a;

   private EK(EI var1) {
      this.a = var1;
   }

   private ChunkAllocator a() {
      EI var1 = this.a;
      var1 = this.a;
      return ev.b(this.a.c);
   }

   public static EK a(EI var0) {
      return new EK(var0);
   }

   private static ChunkAllocator b(EI var0) {
      return ev.b(var0.c);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      EI var1 = this.a;
      var1 = this.a;
      return ev.b(this.a.c);
   }
}
