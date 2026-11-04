package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.valkyrienskies.core.api.ships.PositionedWing;
import org.valkyrienskies.core.api.ships.Wing;
import org.valkyrienskies.core.api.ships.WingGroupChanges;
import org.valkyrienskies.core.util.datastructures.BlockPos2ObjectOpenHashMap;

public final class Ex {
   final BlockPos2ObjectOpenHashMap<Wing> a = new BlockPos2ObjectOpenHashMap<>(0, 0.0F, 3, null);
   List<PositionedWing> b;
   Matrix4dc c = new Matrix4d();
   boolean d = true;
   boolean e;

   public final Matrix4dc a() {
      return this.c;
   }

   public final boolean b() {
      return this.e;
   }

   public final void a(boolean var1) {
      this.e = var1;
   }

   public final boolean a(int var1, int var2, int var3, Wing var4, boolean var5) {
      if (var5) {
         if (this.b == null) {
            this.b = new ArrayList<>();
         }

         List var10000 = this.b;
         Intrinsics.checkNotNull(this.b);
         var10000.add(new PositionedWing(var1, var2, var3, var4));
      }

      return var4 != null ? this.a.put(var1, var2, var3, var4) != null : this.a.remove(var1, var2, var3) != null;
   }

   // $VF: synthetic method
   public static boolean a(Ex var0, int var1, int var2, int var3, Wing var4, boolean var5, int var6) {
      return var0.a(var1, var2, var3, var4, true);
   }

   public final Wing a(int var1, int var2, int var3) {
      return this.a.get(var1, var2, var3);
   }

   public final WingGroupChanges c() {
      return this.b == null && !this.d ? null : new WingGroupChanges(this.b, this.d ? this.c : null);
   }

   public final void d() {
      this.b = null;
      this.d = false;
   }

   public final void a(Matrix4dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.c = var1;
      this.d = true;
   }

   public final void a(WingGroupChanges var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      if (var1.getChangedTransform() != null) {
         Matrix4dc var10001 = var1.getChangedTransform();
         Intrinsics.checkNotNull(var10001);
         this.c = var10001;
      }

      List var10000 = var1.getChangedWings();
      if (var10000 != null) {
         for (PositionedWing var2 : var10000) {
            this.a(var2.getPosX(), var2.getPosY(), var2.getPosZ(), var2.getWing(), false);
         }
      }
   }

   private void a(Function4<? super Integer, ? super Integer, ? super Integer, ? super Wing, Unit> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      BlockPos2ObjectOpenHashMap var2 = this.a;
      if (this.a.getContainsNullKey()) {
         var1.invoke(var2.getKeys()[var2.getN() * 3], var2.getKeys()[var2.getN() * 3 + 1], var2.getKeys()[var2.getN() * 3 + 2], var2.getValues()[var2.getN()]);
      }

      for (int var3 = var2.getN(); var3 >= 0; var3--) {
         if (var2.getKeys()[var3 * 3] != 0 || var2.getKeys()[var3 * 3 + 1] != 0 || var2.getKeys()[var3 * 3 + 2] != 0) {
            var1.invoke(var2.getKeys()[var3 * 3], var2.getKeys()[var3 * 3 + 1], var2.getKeys()[var3 * 3 + 2], var2.getValues()[var3]);
         }
      }
   }

   // $VF: synthetic method
   public static final BlockPos2ObjectOpenHashMap a(Ex var0) {
      return var0.a;
   }
}
