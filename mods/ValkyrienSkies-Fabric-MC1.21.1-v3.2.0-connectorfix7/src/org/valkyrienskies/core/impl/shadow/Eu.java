package org.valkyrienskies.core.impl.shadow;

import java.util.Map;
import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.program.VSCoreInternal;
import org.valkyrienskies.core.internal.ships.VsiMutableQueryableShipData;

public final class Eu implements ek<Et> {
   private final Provider<VsiMutableQueryableShipData<ShipData>> a;
   private final Provider<Ek> b;
   private final Provider<ChunkAllocatorProvider> c;
   private final Provider<EB> d;
   private final Provider<Fl> e;
   private final Provider<DN> f;
   private final Provider<Map<String, DP>> g;
   private final Provider<DW> h;
   private final Provider<ShipData.Factory> i;
   private final Provider<VSCoreInternal> j;

   private Eu(
      Provider<VsiMutableQueryableShipData<ShipData>> var1,
      Provider<Ek> var2,
      Provider<ChunkAllocatorProvider> var3,
      Provider<EB> var4,
      Provider<Fl> var5,
      Provider<DN> var6,
      Provider<Map<String, DP>> var7,
      Provider<DW> var8,
      Provider<ShipData.Factory> var9,
      Provider<VSCoreInternal> var10
   ) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
      this.f = var6;
      this.g = var7;
      this.h = var8;
      this.i = var9;
      this.j = var10;
   }

   private Et a() {
      VsiMutableQueryableShipData var10000 = this.a.get();
      Ek var10001 = this.b.get();
      ChunkAllocatorProvider var10002 = this.c.get();
      EB var10003 = this.d.get();
      Fl var10004 = this.e.get();
      DN var10005 = this.f.get();
      Map var10006 = this.g.get();
      DW var10007 = this.h.get();
      ShipData.Factory var10008 = this.i.get();
      Provider var10 = this.j;
      ShipData.Factory var9 = var10008;
      DW var8 = var10007;
      Map var7 = var10006;
      DN var6 = var10005;
      Fl var5 = var10004;
      EB var4 = var10003;
      ChunkAllocatorProvider var3 = var10002;
      Ek var2 = var10001;
      VsiMutableQueryableShipData var1 = var10000;
      return new Et(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static Eu a(
      Provider<VsiMutableQueryableShipData<ShipData>> var0,
      Provider<Ek> var1,
      Provider<ChunkAllocatorProvider> var2,
      Provider<EB> var3,
      Provider<Fl> var4,
      Provider<DN> var5,
      Provider<Map<String, DP>> var6,
      Provider<DW> var7,
      Provider<ShipData.Factory> var8,
      Provider<VSCoreInternal> var9
   ) {
      return new Eu(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   private static Et a(
      VsiMutableQueryableShipData<ShipData> var0,
      Ek var1,
      ChunkAllocatorProvider var2,
      EB var3,
      Fl var4,
      DN var5,
      Map<String, DP> var6,
      DW var7,
      ShipData.Factory var8,
      Provider<VSCoreInternal> var9
   ) {
      return new Et(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      Eu var1 = this;
      VsiMutableQueryableShipData var10000 = this.a.get();
      Ek var10001 = var1.b.get();
      ChunkAllocatorProvider var10002 = var1.c.get();
      EB var10003 = var1.d.get();
      Fl var10004 = var1.e.get();
      DN var10005 = var1.f.get();
      Map var10006 = var1.g.get();
      DW var10007 = var1.h.get();
      ShipData.Factory var10008 = var1.i.get();
      Provider var10 = var1.j;
      ShipData.Factory var9 = var10008;
      DW var8 = var10007;
      Map var7 = var10006;
      DN var6 = var10005;
      Fl var5 = var10004;
      EB var4 = var10003;
      ChunkAllocatorProvider var3 = var10002;
      Ek var2 = var10001;
      VsiMutableQueryableShipData var11 = var10000;
      return new Et(var11, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }
}
