package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;

public final class EC implements ek<EB> {
   private final Provider<ER> a;
   private final Provider<CY> b;
   private final Provider<ChunkAllocatorProvider> c;
   private final Provider<Fy> d;

   private EC(Provider<ER> var1, Provider<CY> var2, Provider<ChunkAllocatorProvider> var3, Provider<Fy> var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   private EB a() {
      ER var10000 = this.a.get();
      CY var10001 = this.b.get();
      ChunkAllocatorProvider var10002 = this.c.get();
      Fy var4 = this.d.get();
      ChunkAllocatorProvider var3 = var10002;
      CY var2 = var10001;
      ER var1 = var10000;
      return new EB(var1, var2, var3, var4);
   }

   public static EC a(Provider<ER> var0, Provider<CY> var1, Provider<ChunkAllocatorProvider> var2, Provider<Fy> var3) {
      return new EC(var0, var1, var2, var3);
   }

   private static EB a(ER var0, CY var1, ChunkAllocatorProvider var2, Fy var3) {
      return new EB(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      EC var1 = this;
      ER var10000 = this.a.get();
      CY var10001 = var1.b.get();
      ChunkAllocatorProvider var10002 = var1.c.get();
      Fy var4 = var1.d.get();
      ChunkAllocatorProvider var3 = var10002;
      CY var2 = var10001;
      ER var5 = var10000;
      return new EB(var5, var2, var3, var4);
   }
}
