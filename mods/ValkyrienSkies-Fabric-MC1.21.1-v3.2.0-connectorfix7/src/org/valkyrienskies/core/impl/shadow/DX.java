package org.valkyrienskies.core.impl.shadow;

import java.util.Map;
import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;

public final class DX implements ek<DW> {
   private final Provider<Map<String, DP>> a;
   private final Provider<DZ> b;
   private final Provider<ChunkAllocatorProvider> c;

   private DX(Provider<Map<String, DP>> var1, Provider<DZ> var2, Provider<ChunkAllocatorProvider> var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private DW a() {
      Map var10000 = this.a.get();
      DZ var10001 = this.b.get();
      ChunkAllocatorProvider var3 = this.c.get();
      DZ var2 = var10001;
      Map var1 = var10000;
      return new DW(var1, var2, var3);
   }

   public static DX a(Provider<Map<String, DP>> var0, Provider<DZ> var1, Provider<ChunkAllocatorProvider> var2) {
      return new DX(var0, var1, var2);
   }

   private static DW a(Map<String, DP> var0, DZ var1, ChunkAllocatorProvider var2) {
      return new DW(var0, var1, var2);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      DX var1 = this;
      Map var10000 = this.a.get();
      DZ var10001 = var1.b.get();
      ChunkAllocatorProvider var3 = var1.c.get();
      DZ var2 = var10001;
      Map var4 = var10000;
      return new DW(var4, var2, var3);
   }
}
