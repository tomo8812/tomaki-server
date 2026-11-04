package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ChunkAllocator;

public final class DR implements ek<DQ> {
   private final Provider<ChunkAllocator> a;

   private DR(Provider<ChunkAllocator> var1) {
      this.a = var1;
   }

   private DQ a() {
      ChunkAllocator var1 = this.a.get();
      return new DQ(var1);
   }

   public static DR a(Provider<ChunkAllocator> var0) {
      return new DR(var0);
   }

   private static DQ a(ChunkAllocator var0) {
      return new DQ(var0);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      ChunkAllocator var1 = this.a.get();
      return new DQ(var1);
   }
}
