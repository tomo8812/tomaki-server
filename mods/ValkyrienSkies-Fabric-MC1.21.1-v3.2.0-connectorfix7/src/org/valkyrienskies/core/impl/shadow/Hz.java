package org.valkyrienskies.core.impl.shadow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class Hz implements He {
   private final Map<Integer, Hx<?>> b = new HashMap<>();
   private int c;
   private boolean d;

   @Override
   public final List<He.c> a(Vector3dc var1, double var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      return CollectionsKt.emptyList();
   }

   @Override
   public final void h() {
   }

   @Override
   public final <T extends GW> Hc<T> a(T var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      int var3 = this.c++;
      int var2 = var3;
      Hx var6 = new Hx<>(var2, this, Hg.a.a(), new Hb(0.0, new Matrix3d()), true, 1.0, 1.0, 1.0, 1.0, new Vector3d(), 1.0, true, 0, var1, null);
      Map var4 = this.b;
      Integer var5 = var2;
      var4.put(var5, var6);
      return var6;
   }

   @Override
   public final boolean a(int var1) {
      Hx var2;
      if ((var2 = this.b.remove(var1)) != null) {
         boolean var3 = true;
         var2.a = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void b() {
      Map var1 = this.b;
      Iterator var3 = this.b.entrySet().iterator();

      while (var3.hasNext()) {
         Hx var10000 = (Hx)((Entry)var3.next()).getValue();
         boolean var2 = true;
         var10000.a = true;
      }

      this.b.clear();
      this.d = true;
   }

   @Override
   public final boolean g() {
      return this.d;
   }

   @Override
   public final boolean a(HM var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      return false;
   }

   @Override
   public final boolean a(HM var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return false;
   }

   @Override
   public final boolean b(int var1) {
      return false;
   }

   @Override
   public final List<HM> d() {
      return CollectionsKt.emptyList();
   }

   @Override
   public final List<HM> e() {
      return CollectionsKt.emptyList();
   }

   @Override
   public final List<HM> f() {
      return CollectionsKt.emptyList();
   }

   @Override
   public final void a(int var1, int var2) {
   }

   @Override
   public final void b(int var1, int var2) {
   }

   @Override
   public final Hh a(Vector3dc var1, Vector3dc var2, double var3, int... var5) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return null;
   }

   @Override
   public final Hi b(Vector3dc var1, Vector3dc var2, double var3, int... var5) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return null;
   }

   @Override
   public final void a(Integer var1) {
   }

   @Override
   public final List<HW> c() {
      return CollectionsKt.emptyList();
   }
}
