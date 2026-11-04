package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import javax.inject.Inject;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.physics.blockstates.LiquidState;
import org.valkyrienskies.core.api.physics.blockstates.SolidState;
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState;

public final class CP {
   private int i = 1;
   private int j = 1;
   private int k = 1;
   public final Object2IntOpenHashMap<SolidState> a;
   public final Object2IntOpenHashMap<LiquidState> b;
   public final Object2IntOpenHashMap<VsiBlockState> c;
   private final Int2ObjectOpenHashMap<VsiBlockState> l;
   public final Int2ObjectMap<VsiBlockState> d;
   public final Object2IntMap<SolidState> e;
   public final Object2IntMap<LiquidState> f;
   public final Object2IntMap<VsiBlockState> g;
   public boolean h;

   @Inject
   public CP() {
      Object2IntOpenHashMap var1;
      Object2IntOpenHashMap var2 = var1 = new Object2IntOpenHashMap();
      CP var3 = this;
      var2.defaultReturnValue(-1);
      var3.a = var1;
      var2 = var1 = new Object2IntOpenHashMap();
      var3 = this;
      var2.defaultReturnValue(-1);
      var3.b = var1;
      var2 = var1 = new Object2IntOpenHashMap();
      var3 = this;
      var2.defaultReturnValue(-1);
      var3.c = var1;
      this.l = new Int2ObjectOpenHashMap();
      Int2ObjectMap var10001 = Int2ObjectMaps.unmodifiable((Int2ObjectMap)this.l);
      Intrinsics.checkNotNullExpressionValue(var10001, "");
      this.d = var10001;
      Object2IntMap var10 = Object2IntMaps.unmodifiable((Object2IntMap)this.a);
      Intrinsics.checkNotNullExpressionValue(var10, "");
      this.e = var10;
      Object2IntMap var11 = Object2IntMaps.unmodifiable((Object2IntMap)this.b);
      Intrinsics.checkNotNullExpressionValue(var11, "");
      this.f = var11;
      Object2IntMap var12 = Object2IntMaps.unmodifiable((Object2IntMap)this.c);
      Intrinsics.checkNotNullExpressionValue(var12, "");
      this.g = var12;
      this.l.put(0, new VsiBlockState(null, null));
      this.c.put(new VsiBlockState(null, null), 0);
      this.a.put(null, 0);
      this.b.put(null, 0);
   }

   public final Int2ObjectMap<VsiBlockState> a() {
      return this.d;
   }

   public final Object2IntMap<SolidState> b() {
      return this.e;
   }

   public final Object2IntMap<LiquidState> c() {
      return this.f;
   }

   public final Object2IntMap<VsiBlockState> d() {
      return this.g;
   }

   public final boolean e() {
      return this.h;
   }

   public final void f() {
      this.h = true;
   }

   public final int a(SolidState var1) {
      return this.a.getInt(var1);
   }

   public final int a(LiquidState var1) {
      return this.b.getInt(var1);
   }

   public final int a(VsiBlockState var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return this.c.getInt(var1);
   }

   private boolean b(SolidState var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return this.a.containsKey(var1);
   }

   private boolean b(LiquidState var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return this.b.containsKey(var1);
   }

   private boolean c(VsiBlockState var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return this.c.containsKey(var1);
   }

   private final void a(Object var1) {
      if (this.h) {
         throw new IllegalStateException("Valkyrien Skies: tried registering state " + var1 + ", but the block state registry has already been frozen");
      }
   }

   private final int c(SolidState var1) {
      this.a((Object)var1);
      int var2 = this.i;
      int var3;
      if ((var3 = this.a.putIfAbsent(var1, var2)) == -1) {
         this.i = var2 + 1;
         return var2;
      } else {
         return var3;
      }
   }

   private final int c(LiquidState var1) {
      this.a((Object)var1);
      int var2 = this.j;
      int var3;
      if ((var3 = this.b.putIfAbsent(var1, var2)) == -1) {
         this.j = var2 + 1;
         return var2;
      } else {
         return var3;
      }
   }

   public final int b(VsiBlockState var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a((Object)var1);
      SolidState var10000 = var1.getSolidState();
      if (var10000 != null) {
         SolidState var2 = var10000;
         SolidState var3 = var2;
         CP var5 = this;
         this.a((Object)var3);
         int var4 = var5.i;
         int var9;
         if ((var9 = var5.a.putIfAbsent(var3, var4)) == -1) {
            var5.i = var4 + 1;
         }
      }

      LiquidState var14 = var1.getLiquidState();
      if (var14 != null) {
         LiquidState var6 = var14;
         LiquidState var10 = var6;
         CP var7 = this;
         this.a((Object)var10);
         int var13 = var7.j;
         int var11;
         if ((var11 = var7.b.putIfAbsent(var10, var13)) == -1) {
            var7.j = var13 + 1;
         }
      }

      int var8 = this.k;
      int var12;
      if ((var12 = this.c.putIfAbsent(var1, var8)) == -1) {
         this.l.put(var8, var1);
         this.k = var8 + 1;
         return var8;
      } else {
         return var12;
      }
   }
}
